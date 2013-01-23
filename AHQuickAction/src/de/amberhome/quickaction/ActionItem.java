package de.amberhome.quickaction;

import android.graphics.drawable.Drawable;
import anywheresoftware.b4a.BA.ShortName;
/**
 * This object can hold a menu Item for the popup windows with icon and text.
 */
@ShortName("AHActionItem")

public class ActionItem {
	private Drawable icon;
//	private Bitmap thumb;
	private String title;
	private int actionId = -1;
    private boolean selected;
    private boolean sticky;
    private Object mTag;
	    
    /**
     * Constructor
     */
    public ActionItem() {
    }
    
    /**
     * Initializes the action item
     *
     * ActionId - Id for the action item. Pass any number here.
     * Title - Text for the item
     * Icon - Icon for the item. Pass Null if you want a text only item.
     */
    public void Initialize(int ActionId, String Title, Drawable Icon) {
        this.title = Title;
        this.icon = Icon;
        this.actionId = ActionId;
    }
    
    /**
     * Set or get action tag object
     * 
     * Tag - Tag object to set
     */
    public void setTag(Object Tag) {
    	this.mTag = Tag;
    }
    
    public Object getTag() {
    	return this.mTag;
    }
    
	/**
	 * Set or get action title
	 * 
	 * Title - Action title
	 */
	public void setTitle(String Title) {
		this.title = Title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Set or get action icon
	 * 
	 * Icon - action icon as a drawable
	 */
	public void setIcon(Drawable Icon) {
		this.icon = Icon;
	}
	
	public Drawable getIcon() {
		return this.icon;
	}
	
	 /**
     * Set or get action id
     * 
     * ActionId - Action id for this action
     */
    public void setActionId(int ActionId) {
        this.actionId = ActionId;
    }
    
    public int getActionId() {
        return actionId;
    }
    
    /**
     * Set or get sticky status of button
     * 
     * Sticky - true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean Sticky) {
        this.sticky = Sticky;
    }
    
    public boolean getSticky() {
        return sticky;
    }
    
	/**
	 * Set or get selected flag;
	 * 
	 * Selected - Flag to indicate the item is selected
	 */
	public void setSelected(boolean Selected) {
		this.selected = Selected;
	}
	
	public boolean getSelected() {
		return this.selected;
	}

	/**
	 * Set thumb
	 * 
	 * @param thumb Thumb image
	 */
//	public void setThumb(Bitmap thumb) {
//		this.thumb = thumb;
//	}
	
	/**
	 * Get thumb image
	 * 
	 * @return Thumb image
	 */
//	public Bitmap getThumb() {
//		return this.thumb;
//	}
}