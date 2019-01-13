package michaelbrabec.bakalab.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.fragments.UkolyPageFragment;

public class UkolyPagerAdapter extends FragmentStatePagerAdapter {

    private UkolyPageFragment activeTab = new UkolyPageFragment();
    private UkolyPageFragment finishedTab = new UkolyPageFragment();
    private Context mContext;


    public UkolyPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return activeTab;
        } else {
            return finishedTab;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.homework_todo);
            case 1:
                return mContext.getString(R.string.homework_done);
            default:
                return null;
        }
    }

}
