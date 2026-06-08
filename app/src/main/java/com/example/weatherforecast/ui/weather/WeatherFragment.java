package com.example.weatherforecast.ui.weather;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weatherforecast.R;
import com.example.weatherforecast.ui.weather.recommended.RecommendedFragment;
import com.example.weatherforecast.ui.weather.today.TodayFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WeatherFragment extends Fragment {
    private ViewPager2 viewPager2;  // 使用 ViewPager2
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        viewPager2 = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        // 设置适配器
        viewPager2.setAdapter(new WeatherPagerAdapter(this));

        // 关联 TabLayout 和 ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("今日");
                    break;
                case 1:
                    tab.setText("推荐");
                    break;
            }
        }).attach();

        return view;
    }

    // 自定义适配器
    private class WeatherPagerAdapter extends FragmentStateAdapter {

        public WeatherPagerAdapter(Fragment fragment) {
            super(fragment.getChildFragmentManager(), fragment.getLifecycle());
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new TodayFragment();
                case 1:
                    return new RecommendedFragment();
                default:
                    throw new IllegalArgumentException("Invalid position: " + position);
            }
        }

        @Override
        public int getItemCount() {
            return 2; // 总共2个页面
        }
    }
}
