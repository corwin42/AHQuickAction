package de.amberhome.quickaction;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.keywords.Common;

@Events(values = { "Click (Position as Int, ActionItemID as Int)", "Dismiss" })
@BA.ShortName("AHPopupMenu")
public class ICSMenu extends QuickActionBase implements
		PopupWindow.OnDismissListener {
	private View mRootView;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private ScrollView mScroller;
	private List<ActionItem> actionItems = new ArrayList<ActionItem>();
	private int mChildPos;
	private int mInsertPos;
	private int mAnimStyle;
	private int rootWidth = 0;
	public static final int VERTICAL = 1;
	public static final int ANIM_REFLECT = 4;

	/**
     * Constructor for default vertical layout
     * 
     * @param context  Context
     */
    public ICSMenu() {
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

		mInflater = ((LayoutInflater) ba.context
				.getSystemService("layout_inflater"));

		setRootViewId(RHelper.getResourceId("layout", "ahpm_popup_vertical"));

		mAnimStyle = ANIM_REFLECT;
		mChildPos = 0;

		if (ba.subExists(eventName + "_click"))
			super.setOnActionItemClickListener(new QuickActionBase.OnActionItemClickListener() {
				public void onItemClick(QuickActionBase source, int pos,
						int actionId) {
					ba.raiseEvent2(
							this,
							false,
							eventName + "_click",
							true,
							new Object[] { Integer.valueOf(pos),
									Integer.valueOf(actionId) });
				}
			});
		if (ba.subExists(eventName + "_dismiss"))
			super.setOnDismissListener(new QuickActionBase.OnDismissListener() {
				public void onDismiss() {
					ba.raiseEvent2(this, false, eventName + "_dismiss", true,
							(Object[]) null);
				}
			});
	}


	/**
	 * Get number of action items
	 */
	public int getItemCount() {
		return actionItems.size();
	}
	
	public ActionItem GetActionItem(int Index) {
		return (ActionItem) actionItems.get(Index);
	}

	@BA.Hide
	public void setRootViewId(int id) {
		mRootView = ((ViewGroup) mInflater.inflate(id, null));
		mTrack = ((ViewGroup) mRootView.findViewById(RHelper
				.getResourceId("id", "tracks")));

		mScroller = (ScrollView) mRootView.findViewById(RHelper.getResourceId(
				"id", "scroller"));
		
		
		mRootView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		
		
		setContentView(mRootView);
	}

	/**
	 * Set animation style
	 * 
	 * AnimStyle - animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int AnimStyle) {
		mAnimStyle = AnimStyle;
	}

	/**
	 * Add action item
	 * 
	 * Action - AHActionItem object
	 */
	public void AddActionItem(ActionItem Action) {
		actionItems.add(Action);

		String title = Action.getTitle();
		Drawable icon = Action.getIcon();
		View container;
		container = mInflater.inflate(
				RHelper.getResourceId("layout", "ahpm_action_item_vertical"),
				null);

		ImageView img = (ImageView) container.findViewById(RHelper
				.getResourceId("id", "iv_icon"));
		TextView text = (TextView) container.findViewById(RHelper
				.getResourceId("id", "tv_title"));

		if (icon != null)
			img.setImageDrawable(icon);
		else {
			img.setVisibility(View.GONE);
		}

		if (title != null)
			text.setText(title);
		else {
			text.setVisibility(View.GONE);
		}

		final int pos = mChildPos;
		final int actionId = Action.getActionId();

		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(ICSMenu.this, pos, actionId);
				}

				if (!GetActionItem(pos).getSticky()) {
					mDidAction = true;

					dismiss();
				}
			}
		});

		container.setFocusable(true);
		container.setClickable(true);

		if ((mChildPos != 0)) {
			View separator = mInflater
					.inflate(RHelper.getResourceId("layout",
							"ahpm_vert_separator"), null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			separator.setLayoutParams(params);
			separator.setPadding((int) (5 * Common.Density), 0,
					(int) (5 * Common.Density), 0);
			ImageView imgs = (ImageView) separator.findViewById(RHelper
					.getResourceId("id", "iv_separator"));
			imgs.setImageResource(RHelper.getResourceId("drawable",
					"ahpm_menu_separator"));
			imgs.setMinimumHeight(2);
			imgs.setMinimumWidth(-1);
			imgs.setScaleType(ImageView.ScaleType.FIT_XY);

			mTrack.addView(separator, this.mInsertPos);

			mInsertPos += 1;
		}

		mTrack.addView(container, this.mInsertPos);

		mChildPos += 1;
		mInsertPos += 1;
	}

	/**
	 * Show ICS style popup menu. menu is automatically positioned, on top or
	 * bottom of anchor view.
	 * 
	 * AnchorView - View that is used as an anchor
	 */
	@SuppressWarnings("deprecation")
	@Hide
	public void Show2(View AnchorView) {
		preShow();

		int xPos, yPos;

		mDidAction = false;

		int[] location = new int[2];

		AnchorView.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ AnchorView.getWidth(), location[1] + AnchorView.getHeight());

		// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - AnchorView.getWidth());
			xPos = (xPos < 0) ? 0 : xPos;
		} else {
			if (AnchorView.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = (int) (15 * Common.Density);
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;
		}

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		mWindow.showAtLocation(AnchorView, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Show ICS style popup menu. menu is automatically positioned, on top or
	 * bottom of anchor view.
	 * 
	 * AnchorView - View that is used as an anchor
	 */
	@SuppressWarnings("deprecation")
	public void Show(View AnchorView) {
		preShow();

		int xPos, yPos;

		mDidAction = false;

		int[] location = new int[2];

		AnchorView.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ AnchorView.getWidth(), location[1] + AnchorView.getHeight());

		// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		//mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LayoutParams l = mScroller.getLayoutParams();
		l.height = LayoutParams.WRAP_CONTENT;
		
		mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED));
		int rootHeight = mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// automatically get X coord of popup (top left)
		xPos = anchorRect.centerX() - rootWidth / 2;
		
		//Common.Log("rootWidth: " + rootWidth);
		//Common.Log("AnchorView.Left: " + loc[0]);
		//Common.Log("xPos: " + xPos);
		
//		if ((anchorRect.left + rootWidth) > screenWidth) {
//			xPos = anchorRect.left - (rootWidth - AnchorView.getWidth());
//			xPos = (xPos < 0) ? 0 : xPos;
//		} else {
//			if (AnchorView.getWidth() > rootWidth) {
//				xPos = anchorRect.centerX() - (rootWidth / 2);
//			} else {
//				xPos = anchorRect.left;
//			}
//		}

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
				yPos = (int) (StatusBarHeight);
				//LayoutParams l = mScroller.getLayoutParams();
				l.height = (int) (dyTop - StatusBarHeight);
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				//LayoutParams l = mScroller.getLayoutParams();
				l.height = (int) (dyBottom);
			}
		}

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		mWindow.showAtLocation(AnchorView, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void setAnimationStyle(int screenWidth, int requestedX,
			boolean onTop) {
		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle(onTop ? RHelper.getResourceId(
					"style", "Animations.PopUpMenu.Left") : RHelper
					.getResourceId("style", "Animations.PopDownMenu.Left"));
			break;
		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle(onTop ? RHelper.getResourceId(
					"style", "Animations.PopUpMenu.Right") : RHelper
					.getResourceId("style", "Animations.PopDownMenu.Right"));
			break;
		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle(onTop ? RHelper.getResourceId(
					"style", "Animations.PopUpMenu.Center") : RHelper
					.getResourceId("style", "Animations.PopDownMenu.Center"));
			break;
		case ANIM_REFLECT:
			mWindow.setAnimationStyle(onTop ? RHelper.getResourceId(
					"style", "Animations.PopUpMenu.Reflect") : RHelper
					.getResourceId("style", "Animations.PopDownMenu.Reflect"));
		case ANIM_AUTO:
			mWindow.setAnimationStyle(onTop ? RHelper.getResourceId(
					"style", "Animations.PopUpMenu.Reflect") : RHelper
					.getResourceId("style", "Animations.PopDownMenu.Reflect"));
		}
	}
}