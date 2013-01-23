package de.amberhome.quickaction;

import android.widget.PopupWindow.OnDismissListener;
import anywheresoftware.b4a.BA.Hide;


public class QuickActionBase extends PopupWindows implements OnDismissListener {

	protected OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;
	protected boolean mDidAction;
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 5;

	/**
	 * Listener for item click
	 *
	 */
	@Hide
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickActionBase source, int pos, int actionId);
	}

	/**
	 * Set listener for action item clicked.
	 * 
	 * @param listener Listener
	 */
	@Hide
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	@Hide
	public void setOnDismissListener(QuickActionBase.OnDismissListener listener) {
		setOnDismissListener(this);
		
		mDismissListener = listener;
	}

	@Hide
	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	@Hide
	public interface OnDismissListener {
		public abstract void onDismiss();
	}

	@Hide
	public QuickActionBase() {
		super();
	}
}