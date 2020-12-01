package com.lijangop.commoncode.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lijangop.commoncode.R;

/**
 * @Author lijiangop
 * @CreateTime 2020/8/24 16:07
 */
public class ExampleFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = view.findViewById(R.id.tv);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String content = arguments.getString("content");
            tv.setText(TextUtils.isEmpty(content) ? "empty" : content);
        }
    }
}
