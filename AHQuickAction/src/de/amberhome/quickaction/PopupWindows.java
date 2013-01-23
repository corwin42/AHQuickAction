package de.amberhome.quickaction;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import anywheresoftware.b4a.BA.Hide;

/**
 * Custom popup window.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class PopupWindows {
	protected Context mContext;
	protected PopupWindow mWindow;
	protected View mRootView;
	protected Drawable mBackground = null;
	protected WindowManager mWindowManager;
	
	/**
	 * Constructor.
	 * 
	 * @param context Context
	 */
	@Hide
	public void Initialize(Context context) {
		mContext	= context;
		mWindow 	= new PopupWindow(context);

		mWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mWindow.dismiss();
					
					return true;
				}
				
				return false;
			}
		});

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}
	
	public PopupWindows() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * On dismiss
	 */
	protected void onDismiss() {		
	}
	
	/**
	 * On show
	 */
	protected void onShow() {		
	}

	/**
	 * On pre show
	 */
	@SuppressWarnings("deprecation")
	protected void preShow() {
		if (mRootView == null) 
			throw new IllegalStateException("setContentView was not called with a view to display.");
	
		onShow();

		if (mBackground == null) 
			mWindow.setBackgroundDrawable(new BitmapDrawable());
		else 
			mWindow.setBackgroundDrawable(mBackground);

		mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setTouchable(true);
		mWindow.setFocusable(true);
		mWindow.setOutsideTouchable(true);

		mWindow.setContentView(mRootView);
	}

	/**
	 * Set background drawable.
	 * 
	 * @param background Background drawable
	 */
	@Hide
	public void setBackgroundDrawable(Drawable background) {
		mBackground = background;
	}

	/**
	 * Set content view.
	 * 
	 * @param root Root view
	 */
	@Hide
	public void setContentView(View root) {
		mRootView = root;
		
		mWindow.setContentView(root);
	}

	/**
	 * Set content view.
	 * 
	 * @param layoutResID Resource id
	 */
	@Hide
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * Set listener on window dismissed.
	 * 
	 * @param listener
	 */
	@Hide
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mWindow.setOnDismissListener(listener);  
	}

	/**
	 * Dismiss the popup window.
	 */
	@Hide
	public void dismiss() {
		mWindow.dismiss();
	}
}