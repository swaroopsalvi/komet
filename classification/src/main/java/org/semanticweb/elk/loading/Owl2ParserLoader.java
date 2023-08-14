/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semanticweb.elk.loading;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * An {@link AxiomLoader} that loads an ontology using a provided
 * {@link Owl2Parser}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class Owl2ParserLoader extends AbstractAxiomLoader implements
		AxiomLoader {

	/**
	 * a special batch to detect that all axioms are loaded
	 */
	private static final ArrayList<ElkAxiom> POISON_BATCH_ = new ArrayList<ElkAxiom>(
			1);
	/**
	 * the parser used to provide the axioms
	 */
	private final Owl2Parser parser_;
	/**
	 * a bounded queue through which batches of axioms are exchanged between the
	 * parser and the axiom loader; if the queue is full the parser will block
	 * until the next axiom batch is taken
	 */
	private final BlockingQueue<ArrayList<ElkAxiom>> axiomExchanger_;
	/**
	 * the maximum number of axioms in the exchange batch
	 */
	private final int batchLength_;
	/**
	 * the thread in which the parser is running
	 */
	private final Thread parserThread_;
	/**
	 * {@code true} if the parser thread has started
	 */
	private boolean started_;
	/**
	 * {@code true} if the parser has finished processing the ontology
	 */
	private volatile boolean finished_;
	/**
	 * the exception created if something goes wrong
	 */
	protected volatile ElkLoadingException exception;

	/**
	 * Creating an {@link Owl2ParserLoader}, which loads axioms generated using
	 * {@link Owl2Parser}. The axioms generated by the parser are stored in a
	 * buffer of the given {@code bufferSize}.
	 * 
	 * @param interrupter
	 *            the {@link InterruptMonitor} that is checked for interruptions
	 * @param owlParser
	 *            the parser used to load the ontology
	 * @param batchLength
	 *            the size of the batch for exchanging axioms
	 */
	public Owl2ParserLoader(final InterruptMonitor interrupter,
			Owl2Parser owlParser, int batchLength) {
		super(interrupter);
		this.parser_ = owlParser;
		this.axiomExchanger_ = new SynchronousQueue<ArrayList<ElkAxiom>>();
		this.batchLength_ = batchLength;
		this.finished_ = false;
		this.parserThread_ = new Thread(new Parser(), "elk-parser-thread");
		parserThread_.setDaemon(true);
		this.started_ = false;
		this.exception = null;
	}

	/**
	 * Creating an {@link Owl2ParserLoader}, which loads axioms generated using
	 * {@link Owl2Parser}.
	 * 
	 * @param interrupter
	 *            the {@link InterruptMonitor} that is checked for interruptions
	 * @param owlParser
	 *            the parser used to load the ontology
	 */
	public Owl2ParserLoader(final InterruptMonitor interrupter,
			Owl2Parser owlParser) {
		this(interrupter, owlParser, 128);
	}

	@Override
	public synchronized void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {
		if (finished_)
			return;

		if (!started_) {
			parserThread_.start();
			started_ = true;
		}

		ArrayList<ElkAxiom> nextBatch;

		for (;;) {
			if (isInterrupted())
				break;
			try {
				nextBatch = axiomExchanger_.take();
			} catch (InterruptedException e) {
				/*
				 * we don't know for sure why the thread was interrupted, so we
				 * need to obey; if interrupt was not relevant, the process will
				 * restart; we need to restore the interrupt status so that the
				 * called methods know that there was an interrupt
				 */
				Thread.currentThread().interrupt();
				break;
			}
			if (nextBatch == POISON_BATCH_) {
				break;
			}
			for (int i = 0; i < nextBatch.size(); i++) {
				ElkAxiom axiom = nextBatch.get(i);
				axiomInserter.visit(axiom);
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	@Override
	public synchronized void dispose() {
		disposeParserResources();
		this.axiomExchanger_.clear();
	}

	/**
	 * The parser worker used to parse the ontology
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class Parser implements Runnable {
		@Override
		public void run() {
			try {
				parser_.accept(new AxiomInserter(axiomExchanger_, batchLength_));
			} catch (Throwable e) {
				exception = new ElkLoadingException(
						"Cannot load the ontology!", e);
			} finally {
				finished_ = true;
				try {
					axiomExchanger_.put(POISON_BATCH_);
				} catch (InterruptedException e) {
					/*
					 * we don't know what is causing this but we need to obey;
					 * consistency of the computation for such interrupt is not
					 * guaranteed; restore the interrupt status and exit
					 */
					Thread.currentThread().interrupt();
				}
				disposeParserResources();
			}
		}
	}

	/**
	 * A simple {@link ElkAxiomProcessor} that insert the parsed axioms into the
	 * given queue
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class AxiomInserter implements Owl2ParserAxiomProcessor {

		final private BlockingQueue<ArrayList<ElkAxiom>> axiomBuffer_;
		private final int batchLength_;

		/**
		 * the next batch of axioms that should be filled
		 */
		private ArrayList<ElkAxiom> nextBatch_;

		AxiomInserter(BlockingQueue<ArrayList<ElkAxiom>> axiomBuffer,
				int batchLength) {
			this.axiomBuffer_ = axiomBuffer;
			this.batchLength_ = batchLength;
			nextBatch_ = new ArrayList<ElkAxiom>(batchLength_);
		}

		@Override
		public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
			nextBatch_.add(elkAxiom);
			if (nextBatch_.size() == batchLength_) {
				submitBatch();
				nextBatch_ = new ArrayList<ElkAxiom>(batchLength_);
			}
		}

		@Override
		public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
			// No additional prefixes can be registered
		}

		@Override
		public void finish() throws Owl2ParseException {
			// submit the last partially filled batch
			submitBatch();
		}

		private void submitBatch() throws Owl2ParseException {
			try {
				axiomBuffer_.put(nextBatch_);
			} catch (InterruptedException e) {
				throw new Owl2ParseException("ELK Parser was interrupted", e);
			}
		}
	}

	@Override
	public boolean isLoadingFinished() {
		return finished_;
	}

	/**
	 * A hook to free resources used by the parser
	 */
	protected void disposeParserResources() {
		// does nothing so far
	}

}
