//
// Version History:
// V1.00:
//    - initial version
//
// V1.01:
//    - Changed all Methods to Uppercase
//    - Fixed positioning/sizing problem with QuickAction3D 
//    - Added ICS Style Menu
//    - Added ItemCount property to get number of items
//    - fixed animations
// V1.02:
//    - Fixed Positioning of Quickaction (sometimes didn't show above anchor)

package de.amberhome.quickaction;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.keywords.Common;

@Version(1.02f)
@Author("Markus Stipp")

@Events(values={"Click (Position as Int, ActionItemID as Int)", "Dismiss"})
@ShortName("AHQuickAction")
/**
 * This object provides a horizontally aligned QuickAction popup window.
 */
public class QuickAction extends QuickActionBase implements OnDismissListener {
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private Animation mTrackAnim;
	private LayoutInflater inflater;
	private ViewGroup mTrack;
	private List<ActionItem> mActionItemList = new ArrayList<ActionItem>();
	
	private boolean mAnimateTrack;
	
	private int mChildPos;    
    private int mAnimStyle;

	/**
	 * This library provides two objects for some nice looking popup menus.
	 * 
	 *  The AHQuickAction object is a popup window with horizontally aligned items.
	 *  You can see this popup window in many apps like Tapatalk or Folder Organizer.
	 *  
	 *  The AHQuickAction3D object is a popup that can be used horizontally and
	 *  vertically. You can find a similar popup in many Google apps like 3D-Gallery
	 *  and Text&Tables.
	 *  
	 *  This library is somewhat different to other libraries because it makes use
	 *  of standard Android resource files. Resource files cannot be published inside
	 *  a .jar file and so you have to copy them to your Basic4Android project directory.
	 *  
	 *  Copy the whole "res" directory structure to the "Objects" directory of your B4A
	 *  project and VERY IMPORTANT: make them READ ONLY! Otherwise B4A will delete them
	 *  when compiling. After you make any change to the Objecs/res directory use
	 *  "Clean project" menu item in B4A to force the compiler to recreate the R.java file.
	 *  
	 *  If you forget to make the resource files read only then you will most likely get
	 *  a ResourceNotFound exception when you try to use the popup objects.
	 *  
	 *  The objects are ported from open source projects and you can find them here:
	 *  <link>NewQuickAction|https://github.com/lorensiuswlt/NewQuickAction</link>
	 *  <link>NewQuickAction3D|https://github.com/lorensiuswlt/NewQuickAction3D</link>
	 */
	public static void LIBRARY_DOC() {
	}
    
	/**
     * Constructor for default vertical layout
     * 
     * @param context  Context
     */
    public QuickAction() {
    	super();
    }


    /**
     * Initialize the AHQuickAction3D object
     * 
     * EventName - Event name
     * 
     */
	public void Initialize(final BA ba, String EventName) {
		super.Initialize(ba.context);

		final String eventName = EventName.toLowerCase();
		
		inflater 	= (LayoutInflater) ba.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTrackAnim 	= AnimationUtils.loadAnimation(ba.context, RHelper.getResourceId("anim", "ahqa_rail"));
		
		mTrackAnim.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
	              // Pushes past the target area, then snaps back into place.
	                // Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = (t * 1.55f) - 1.1f;
				
	            return 1.2f - inner * inner;
	        }
		});
	        
		setRootViewId(RHelper.getResourceId("layout", "ahqa_quickaction"));
		
		mAnimStyle		= ANIM_AUTO;
		mAnimateTrack	= true;
		mChildPos		= 0;
		
		if (ba.subExists(eventName + "_click")) {
			super.setOnActionItemClickListener(new QuickActionBase.OnActionItemClickListener() {
				
				@Override
				public void onItemClick(QuickActionBase source, int pos, int actionId) {
					ba.raiseEvent2(this, false, eventName + "_click", true, new Object[] {Integer.valueOf(pos), Integer.valueOf(actionId)});					
				}
			});
		}
		
		if (ba.subExists(eventName + "_dismiss")) {
			super.setOnDismissListener(new QuickActionBase.OnDismissListener() {
				
				@Override
				public void onDismiss() {
					ba.raiseEvent2(this, false, eventName + "_dismiss", true, (Object[]) null);
					
				}
			});
		}

	}

	/**
	 * Get number of action items
	 */
	public int getItemCount() {
		return mActionItemList.size();
	}
	
    /**
     * Get action item at an index
     * 
     * Index - Index of item
     * 
     */
    public ActionItem GetActionItem(int Index) {
        return mActionItemList.get(Index);
    }
    
	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
    @Hide
	public void setRootViewId(int id) {
		mRootView	= (ViewGroup) inflater.inflate(id, null);
		mTrack 		= (ViewGroup) mRootView.findViewById(RHelper.getResourceId("id", "tracks"));

		mArrowDown 	= (ImageView) mRootView.findViewById(RHelper.getResourceId("id", "arrow_down"));
		mArrowUp 	= (ImageView) mRootView.findViewById(RHelper.getResourceId("id", "arrow_up"));

		//This was previously defined on show() method, moved here to prevent force close that occured
		//when tapping fastly on a view to show quickaction dialog.
		//Thanx to zammbi (github.com/zammbi)
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		setContentView(mRootView);
	}
	
	/**
	 * Flag, if the track should be animated when the popup window opens.
	 * 
	 */
	public void setAnimateTrack(boolean mAnimateTrack) {
		this.mAnimateTrack = mAnimateTrack;
	}
	
	/**
	 * Set animation style
	 * 
	 * AnimStyle - animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int AnimStyle) {
		this.mAnimStyle = AnimStyle;
	}

//	private void RemoveActionItem(int Index) {
//		mWindow.dismiss();
//		
//		ActionItem ac = mActionItemList.get(Index);
//
//		for (int i = 0; i < mTrack.getChildCount(); i++) {
//			View container = mTrack.getChildAt(i);
//			if (((ActionItem)container.getTag()) == ac) {
//				mTrack.removeViewAt(i);
//			}
//		}
//		
//		mActionItemList.remove(Index);
//		mChildPos--;
//		
//	}
	
	/**
	 * Add action item
	 * 
	 * Action - AHActionItem object
	 */
	public void AddActionItem(ActionItem Action) {
		mActionItemList.add(Action);
		
		String title 	= Action.getTitle();
		Drawable icon 	= Action.getIcon();
		
		View container	= (View) inflater.inflate(RHelper.getResourceId("layout", "ahqa_action_item"), null);
		
		ImageView img 	= (ImageView) container.findViewById(RHelper.getResourceId("id", "iv_icon"));
		TextView text 	= (TextView) container.findViewById(RHelper.getResourceId("id", "tv_title"));
		
		if (icon != null) { 
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}
		
		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}
		
		final int pos 		=  mChildPos;
		final int actionId 	= Action.getActionId();
		
		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(QuickAction.this, pos, actionId);
                }
				
                if (!GetActionItem(pos).getSticky()) {  
                	mDidAction = true;
                	
                	//workaround for transparent background bug
                	//thx to Roman Wozniak <roman.wozniak@gmail.com>
                	v.post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }
			}
		});
		
		container.setFocusable(true);
		container.setClickable(true);
		container.setTag(Action);
			 
		mTrack.addView(container, mChildPos+1);
		
		mChildPos++;
	}
	
	/**
	 * Clears the list of action items.
	 * 
	 * Attention: Be shure not to call this if the popup window is open. Your app will crash!
	 */
//	private void Clear() {
//		mWindow.dismiss();
//		for (int i = 0; i < mTrack.getChildCount(); i++) {
//			View container = mTrack.getChildAt(i);
//			if (container.getTag().getClass() == ActionItem.class) {
//				mTrack.removeViewAt(i);
//			}
//		}
//		mActionItemList.clear();
//		mChildPos = 0;
//		setRootViewId(RHelper.getResourceId("layout", "ahqa_quickaction"));
//	}
	
    /**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 * AnchorView - View that is used as an anchor
	 */
	@SuppressWarnings("deprecation")
    public void Show (View AnchorView) {
        preShow();

        int[] location         = new int[2];
        int xOffset            = 0;
        int xPos, yPos;
        int arrowOffset        = 0;
        
        mDidAction             = false;
        
        AnchorView.getLocationOnScreen(location);

        Rect anchorRect     = new Rect(location[0], location[1], location[0] + AnchorView.getWidth(), location[1] 
                            + AnchorView.getHeight());

        //mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED));
        
        //int rootWidth         = mRootView.getMeasuredWidth(); //Not used anymore, Return wrong value
        int rootHeight         = mRootView.getMeasuredHeight();

        int screenWidth     = mWindowManager.getDefaultDisplay().getWidth();
        //int screenHeight     = mWindowManager.getDefaultDisplay().getHeight(); //Appears to not be used

        //xOffset is the amount the popup window will be offset to the anchorView. -500 offsets left 500
        xOffset = -500;
        //arrowOffset is the inverse of xOffset to bring the arrow back to the correct position
        arrowOffset = xOffset * -1;
    
        //Apply xOffset to xPos
        xPos = anchorRect.centerX() + xOffset;
    
        //This bit deals with if the anchorView is very close to the right side of screen as the popup can be auto pushed left,
        //so we need to account for this in arrow position. 580 appears to be width of popup
        if ((anchorRect.centerX() + (580 + xOffset)) > screenWidth){
            arrowOffset = arrowOffset + ((580 + xOffset) - (screenWidth - anchorRect.centerX()));
        }
        
        //This deals with if the anchorView is within 500 of left side of screen
        if (anchorRect.centerX() <= 500){
            arrowOffset = anchorRect.centerX();
        }
        
        //Resets Arrow position for Gingerbread and under
        if (Build.VERSION.SDK_INT <= 10){
            arrowOffset = anchorRect.centerX();
        }
        
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        
		/* Status Bar Height */
		Activity parent = (Activity)AnchorView.getContext();
		Rect rectgle= new Rect();
		Window window = parent.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int StatusBarHeight= rectgle.top;

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = StatusBarHeight;
				LayoutParams l = mTrack.getLayoutParams();
				l.height = (int) (dyTop - 32 * Common.Density - StatusBarHeight);
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = mTrack.getLayoutParams();
				l.height = (int) (dyBottom - 32 * Common.Density);
			}
		}
        
        
        //The requestedX param has changed to apply offset when required.
        showArrow(((onTop) ? RHelper.getResourceId("id", "arrow_down") : RHelper.getResourceId("id", "arrow_up")), arrowOffset);
        
        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
    
        mWindow.showAtLocation(AnchorView, Gravity.NO_GRAVITY, xPos, yPos);
        
        if (mAnimateTrack) mTrack.startAnimation(mTrackAnim);
        
    }
    
    /**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 * AnchorView - View that is used as an anchor
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void ShowTest (View AnchorView) {
		preShow();

		int[] location 		= new int[2];
		
		mDidAction 			= false;
		
		AnchorView.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + AnchorView.getWidth(), location[1] 
		                	+ AnchorView.getHeight());

		//mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED));
		
		int rootWidth 		= mRootView.getMeasuredWidth();
		int rootHeight 		= mRootView.getMeasuredHeight();
		
		Common.Log("RootWidth: " + rootWidth);
		Common.Log("rootHeight: " + rootHeight);

		int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		
		Common.Log("screenWidth: " + screenWidth);
		
		//int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		
		//int screenHeight 	= mWindowManager.getDefaultDisplay().getHeight();

		int xPos 			= (screenWidth - rootWidth) / 2;
		int yPos	 		= anchorRect.top - rootHeight;

		Common.Log("xPos: " + xPos);
		Common.Log("yPos: " + yPos);
		
		boolean onTop		= true;
		
		// display on bottom
		if (rootHeight > AnchorView.getTop()) {
			yPos 	= anchorRect.bottom;
			onTop	= false;
		}

		showArrow(((onTop) ? RHelper.getResourceId("id", "arrow_down") : RHelper.getResourceId("id", "arrow_up")), anchorRect.centerX());
		
		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
	
		mWindow.showAtLocation(AnchorView, Gravity.NO_GRAVITY, xPos, yPos);
		
		if (mAnimateTrack) mTrack.startAnimation(mTrackAnim);
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth Screen width
	 * @param requestedX distance from left screen
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style", "Animations.PopUpMenu.Left") : RHelper.getResourceId("style", "Animations.PopDownMenu.Left"));
			break;
					
		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style", "Animations.PopUpMenu.Right") : RHelper.getResourceId("style", "Animations.PopDownMenu.Right"));
			break;
					
		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style", "Animations.PopUpMenu.Center") : RHelper.getResourceId("style", "Animations.PopDownMenu.Center"));
		break;
					
		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style", "Animations.PopUpMenu.Left") : RHelper.getResourceId("style", "Animations.PopDownMenu.Left"));
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style", "Animations.PopUpMenu.Center") : RHelper.getResourceId("style", "Animations.PopDownMenu.Center"));
			} else {
				mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style", "Animations.PopDownMenu.Right") : RHelper.getResourceId("style", "Animations.PopDownMenu.Right"));
			}
					
			break;
		}
	}
	
	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == RHelper.getResourceId("id", "arrow_up")) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == RHelper.getResourceId("id", "arrow_up")) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        
        param.leftMargin = requestedX - arrowWidth / 2;
      
        hideArrow.setVisibility(View.INVISIBLE);
    }
	
}