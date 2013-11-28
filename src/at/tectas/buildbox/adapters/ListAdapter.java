package at.tectas.buildbox.adapters;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.R;

public class ListAdapter extends ArrayAdapter<String> implements
		ViewPager.OnPageChangeListener {
	private final BuildBoxMainActivity mContext;
	private final ActionBar mActionBar;
	protected SherlockFragment currentFragment = null;
	protected int listItemIndex = 0;
	private final BuildBoxListListener mListener = new BuildBoxListListener();
	protected ArrayList<ArrayList<ListItemInfo>> backStack = new ArrayList<ArrayList<ListItemInfo>>();

	public Fragment getCurrentFragment() {
		return this.currentFragment;
	}

	public int getListItemIndex() {
		return this.listItemIndex;
	}

	public ListAdapter(FragmentActivity activity) {
		super(activity, android.R.layout.simple_list_item_1);
		mContext = (BuildBoxMainActivity) activity;
		mActionBar = ((SherlockFragmentActivity) activity)
				.getSupportActionBar();
		mActionBar.setListNavigationCallbacks(this, mListener);
	}

	public class ListItemInfo {
		public Class<?> clss;
		public Bundle info;

		public ListItemInfo(Class<?> clss, Bundle info) {
			this.clss = clss;
			this.info = info;
		}
	}

	public class BuildBoxListListener implements ActionBar.OnNavigationListener {

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			return replaceFramgent(itemPosition, true);
		}
	}

	public void navigateUp() {
		int backStackSize = this.backStack.get(this.getListItemIndex()).size();
		if (backStackSize > 1) {
			this.backStack.get(this.getListItemIndex()).remove(
					backStackSize - 1);
			replaceFramgent(this.getListItemIndex(), false);
		} else {
			this.mContext.finish();
		}
	}

	public void addFragmentBackStack(Class<? extends SherlockFragment> clss,
			Bundle bundle) {
		this.backStack.get(this.getListItemIndex()).add(
				new ListItemInfo(clss, bundle));
	}

	public boolean replaceFramgent(int position, boolean addToStack) {
		SherlockFragment fragment = null;
		try {
			int size = backStack.get(position).size();
			ListItemInfo info = backStack.get(position).get(size - 1);
			fragment = (SherlockFragment) info.clss.newInstance();
			fragment.setArguments(info.info);
			currentFragment = fragment;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (fragment != null) {
			FragmentTransaction fragmentTransaction = ((SherlockFragmentActivity) mContext)
					.getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.main_container, fragment);
			fragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
			mContext.invalidateOptionsMenu();
			listItemIndex = position;

			return true;
		}
		return false;
	}

	public void changeListItem(int position, int depth, Class<?> clss,
			Bundle info) {
		if (depth == 0) {
			String item = this.getItem(position);
			this.remove(item);
			this.insert(item, position);
		}
		this.backStack.get(position).set(depth, new ListItemInfo(clss, info));
	}

	public void addListItem(String title, Class<?> clss, Bundle info) {
		this.add(title);
		ArrayList<ListItemInfo> list = new ArrayList<ListItemInfo>();
		list.add(new ListItemInfo(clss, info));
		this.backStack.add(list);
	}

	public void destroy() {
		this.destroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}
}