package de.amberhome.quickaction;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.keywords.Common;

@Events(values = { "Click (Position as Int, ActionItemID as Int)", "Dismiss" })
@ShortName("AHQuickAction3D")
/**
 * This object provides a QuickAction3D popup window. The items can be aligned
 * horizontally or vertically.
 */
public class QuickAction3D extends QuickActionBase implements OnDismissListener {
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private ScrollView mScroller;
	private List<ActionItem> actionItems = new ArrayList<ActionItem>();

	private int mChildPos;
	private int mInsertPos;
	private int mAnimStyle;
	private int mOrientation;
	private int rootWidth = 0;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	public static final int ANIM_REFLECT = 4;

	/**
	 * Constructor for default vertical layout
	 * 
	 * @param context
	 *            Context
	 */
	public QuickAction3D() {
		super();
	}

	/**
	 * Initialize the AHQuickAction3D object
	 * 
	 * EventName - Event name Orientation - Orientation of the popup window
	 * 
	 */
	public void Initialize(final BA ba, String EventName, int Orientation) {
		super.Initialize(ba.context);

		final String eventName = EventName.toLowerCase();

		mOrientation = Orientation;

		mInflater = (LayoutInflater) ba.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (mOrientation == HORIZONTAL) {
			setRootViewId(RHelper.getResourceId("layout",
					"ahqa_popup_horizontal"));
		} else {
			setRootViewId(RHelper
					.getResourceId("layout", "ahqa_popup_vertical"));
		}

		mAnimStyle = ANIM_AUTO;
		mChildPos = 0;

		if (ba.subExists(eventName + "_click")) {
			super.setOnActionItemClickListener(new QuickActionBase.OnActionItemClickListener() {

				@Override
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
		}

		if (ba.subExists(eventName + "_dismiss")) {
			super.setOnDismissListener(new QuickActionBase.OnDismissListener() {

				@Override
				public void onDismiss() {
					ba.raiseEvent2(this, false, eventName + "_dismiss", true,
							(Object[]) null);

				}
			});
		}

	}

	/**
	 * Get number of action items
	 */
	public int getItemCount() {
		return actionItems.size();
	}
	
	/**
	 * Get action item at an index
	 * 
	 * Index - Index of item
	 * 
	 */
	public ActionItem GetActionItem(int Index) {
		return actionItems.get(Index);
	}

	/**
	 * Set root view.
	 * 
	 * @param id
	 *            Layout resource id
	 */
	@Hide
	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(RHelper.getResourceId("id",
				"tracks"));

		mArrowDown = (ImageView) mRootView.findViewById(RHelper.getResourceId(
				"id", "arrow_down"));
		mArrowUp = (ImageView) mRootView.findViewById(RHelper.getResourceId(
				"id", "arrow_up"));

		mScroller = (ScrollView) mRootView.findViewById(RHelper.getResourceId(
				"id", "scroller"));

		// This was previously defined on show() method, moved here to prevent
		// force close that occured
		// when tapping fastly on a view to show quickaction dialog.
		// Thanx to zammbi (github.com/zammbi)
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}

	/**
	 * Set animation style
	 * 
	 * AnimStyle - animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int AnimStyle) {
		this.mAnimStyle = AnimStyle;
	}

	/* Currently not working! */
//	private void RemoveActionItem(int Index) {
//		mWindow.dismiss();
//
//		ActionItem ac = actionItems.get(Index);
//
//		for (int i = 0; i < mTrack.getChildCount(); i++) {
//			View container = mTrack.getChildAt(i);
//			if (((ActionItem) container.getTag()) == ac) {
//				mTrack.removeViewAt(i);
//			}
//		}
//
//		actionItems.remove(Index);
//		mChildPos--;
//		mInsertPos--;
//	}

	/**
	 * Add action item
	 * 
	 * Action - AHActionItem object
	 */
	@SuppressWarnings("deprecation")
	public void AddActionItem(ActionItem Action) {
		actionItems.add(Action);

		String title = Action.getTitle();
		Drawable icon = Action.getIcon();

		View container;

		if (mOrientation == HORIZONTAL) {
			container = mInflater.inflate(RHelper.getResourceId("layout",
					"ahqa_action_item_horizontal"), null);
		} else {
			container = mInflater.inflate(RHelper.getResourceId("layout",
					"ahqa_action_item_vertical"), null);
		}

		ImageView img = (ImageView) container.findViewById(RHelper
				.getResourceId("id", "iv_icon"));
		TextView text = (TextView) container.findViewById(RHelper
				.getResourceId("id", "tv_title"));

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

		final int pos = mChildPos;
		final int actionId = Action.getActionId();

		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(QuickAction3D.this, pos,
							actionId);
				}

				if (!GetActionItem(pos).getSticky()) {
					mDidAction = true;

					dismiss();
				}
			}
		});

		container.setFocusable(true);
		container.setClickable(true);
		container.setTag(Action);

		if (mOrientation == HORIZONTAL && mChildPos != 0) {
			View separator = mInflater.inflate(
					RHelper.getResourceId("layout", "ahqa_horiz_separator"),
					null);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);

			separator.setLayoutParams(params);
			separator.setPadding((int) (5 * Common.Density), 0,
					(int) (5 * Common.Density), 0);

			mTrack.addView(separator, mInsertPos);

			mInsertPos++;
		}

		mTrack.addView(container, mInsertPos);

		mChildPos++;
		mInsertPos++;
	}

	/**
	 * Clears the list of action items.
	 * 
	 * Attention: Be shure not to call this if the popup window is open. Your
	 * app will crash!
	 */
//	private void Clear() {
//		mWindow.dismiss();
//		for (int i = 0; i < mTrack.getChildCount(); i++) {
//			View container = mTrack.getChildAt(i);
//			if (container.getTag().getClass() == ActionItem.class) {
//				mTrack.removeViewAt(i);
//			}
//		}
//		actionItems.clear();
//		mChildPos = 0;
//		mInsertPos = 0;
//		setRootViewId(RHelper.getResourceId("layout", "ahqa_quickaction"));
//	}

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or
	 * bottom of anchor view.
	 * 
	 * AnchorView - View that is used as an anchor
	 */
	@SuppressWarnings("deprecation")
	public void Show(View AnchorView) {
		preShow();

		int xPos, yPos, arrowPos;

		mDidAction = false;

		int[] location = new int[2];

		AnchorView.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ AnchorView.getWidth(), location[1] + AnchorView.getHeight());

		// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));

		//mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED));

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

			arrowPos = anchorRect.centerX() - xPos;

		} else {
			if (AnchorView.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}

			arrowPos = anchorRect.centerX() - xPos;
		}

		//Common.Log("rootWidth: " + rootWidth);
		//Common.Log("AnchorView.Left: " + AnchorView.getLeft());
		//Common.Log("xPos: " + xPos);

		
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
				LayoutParams l = mScroller.getLayoutParams();
				l.height = (int) (dyTop - 32 * Common.Density - StatusBarHeight);
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = mScroller.getLayoutParams();
				l.height = (int) (dyBottom - 32 * Common.Density);
			}
		}

		showArrow(((onTop) ? RHelper.getResourceId("id", "arrow_down")
				: RHelper.getResourceId("id", "arrow_up")), arrowPos);

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		mWindow.showAtLocation(AnchorView, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth
	 *            screen width
	 * @param requestedX
	 *            distance from left edge
	 * @param onTop
	 *            flag to indicate where the popup should be displayed. Set TRUE
	 *            if displayed on top of anchor view and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX,
			boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style",
					"Animations.PopUpMenu.Left") : RHelper.getResourceId(
					"style", "Animations.PopDownMenu.Left"));
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style",
					"Animations.PopUpMenu.Right") : RHelper.getResourceId(
					"style", "Animations.PopDownMenu.Right"));
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style",
					"Animations.PopUpMenu.Center") : RHelper.getResourceId(
					"style", "Animations.PopDownMenu.Center"));
			break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId("style",
					"Animations.PopUpMenu.Reflect") : RHelper.getResourceId(
					"style", "Animations.PopDownMenu.Reflect"));
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId(
						"style", "Animations.PopUpMenu.Left") : RHelper
						.getResourceId("style", "Animations.PopDownMenu.Left"));
			} else if (arrowPos > screenWidth / 4
					&& arrowPos < 3 * (screenWidth / 4)) {
				mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId(
						"style", "Animations.PopUpMenu.Center")
						: RHelper.getResourceId("style",
								"Animations.PopDownMenu.Center"));
			} else {
				mWindow.setAnimationStyle((onTop) ? RHelper.getResourceId(
						"style", "Animations.PopUpMenu.Right") : RHelper
						.getResourceId("style", "Animations.PopDownMenu.Right"));
			}

			break;
		}
	}

	/**
	 * Show arrow
	 * 
	 * @param whichArrow
	 *            arrow type resource id
	 * @param requestedX
	 *            distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX) {
		final View showArrow = (whichArrow == RHelper.getResourceId("id",
				"arrow_up")) ? mArrowUp : mArrowDown;
		final View hideArrow = (whichArrow == RHelper.getResourceId("id",
				"arrow_up")) ? mArrowDown : mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow
				.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

		hideArrow.setVisibility(View.INVISIBLE);
	}
}