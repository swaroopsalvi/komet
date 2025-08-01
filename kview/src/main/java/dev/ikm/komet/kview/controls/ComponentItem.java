package dev.ikm.komet.kview.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class ComponentItem {

    public ComponentItem(String text, Image icon) {
        this(text, icon, null);
    }

    public ComponentItem(String text, Image icon, Integer nid) {
        this.text.set(text);
        this.icon.set(icon);
        this.nid = nid;
    }

    // -- text
    private StringProperty text = new SimpleStringProperty();
    public String getText() { return text.get(); }
    public StringProperty textProperty() { return text; }
    public void setText(String text) { this.text.set(text); }

    // -- icon
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    public Image getIcon() { return icon.get(); }
    public ObjectProperty<Image> iconProperty() { return icon; }
    public void setIcon(Image icon) { this.icon.set(icon); }

    // -- nid
    private Integer nid;
    public Integer getNid() { return nid;}
    public void setNid(Integer nid) { this.nid = nid; }
}