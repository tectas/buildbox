package at.tectas.buildbox.adapters;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.fragments.ContentListFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;
import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.msteam.R;

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
		public ArrayList<Item> children;

		public ListItemInfo(Class<?> clss, Bundle info, ArrayList<Item> children) {
			this.clss = clss;
			this.info = info;
			this.children = children;
		}
	}

	public class BuildBoxListListener implements ActionBar.OnNavigationListener {

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			return replaceFragment(itemPosition);
		}
	}

	public void navigateUp() {
		int backStackSize = this.backStack.get(this.getListItemIndex()).size();
		if (backStackSize > 1) {
			this.backStack.get(this.getListItemIndex()).remove(
					backStackSize - 1);
			replaceFragment(this.getListItemIndex());
		} else {
			this.mContext.finish();
		}
	}

	public void addFragmentBackStack(Class<? extends SherlockFragment> clss,
			Bundle bundle, ArrayList<Item> items) {
		this.backStack.get(this.getListItemIndex()).add(
				new ListItemInfo(clss, bundle, items));
	}

	public boolean replaceFragment(int position) {
		SherlockFragment fragment = null;
		try {
			int size = backStack.get(position).size();
			ListItemInfo info = backStack.get(position).get(size - 1);

			if (info.info == null) {
				info.info = new Bundle();
			}
			if (info.info.getInt("index", -1) == -1) {
				info.info.putInt("index", this.listItemIndex);
			}

			fragment = (SherlockFragment) info.clss.newInstance();
			
			if (fragment instanceof ContentListFragment) {
				((ContentListFragment)fragment).setListItems(info.children);
			}
			
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
			Bundle info, ArrayList<Item> items) {
		if (depth == 0) {
			String item = this.getItem(position);
			this.remove(item);
			this.insert(item, position);
		}
		this.backStack.get(position).set(depth, new ListItemInfo(clss, info, items));
	}

	public void addListItem(String title, Class<?> clss, Bundle info) {
		this.add(title);
		ArrayList<ListItemInfo> list = new ArrayList<ListItemInfo>();
		list.add(new ListItemInfo(clss, info, null));
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