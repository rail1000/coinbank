package com.gongchuang.ethtoken.ui.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gongchuang.ethtoken.R;
import com.gongchuang.ethtoken.base.BaseActivity;
import com.gongchuang.ethtoken.domain.VerifyMnemonicWordTag;
import com.gongchuang.ethtoken.ui.adapter.VerifyBackupMnemonicWordsAdapter;
import com.gongchuang.ethtoken.ui.adapter.VerifyBackupSelectedMnemonicWordsAdapter;
import com.gongchuang.ethtoken.utils.AppUtils;
import com.gongchuang.ethtoken.utils.LogUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dwq on 2018/3/22/022.
 * e-mail:lomapa@163.com
 */

public class VerifyMnemonicBackupActivity extends BaseActivity {
    private static final int VERIFY_SUCCESS_RESULT = 2202;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_selected)
    RecyclerView rvSelected;
    @BindView(R.id.rv_mnemonic)
    RecyclerView rvMnemonic;
    private String walletMnemonic;
    private List<VerifyMnemonicWordTag> mnemonicWords;
    private List<String> selectedMnemonicWords;
    private VerifyBackupMnemonicWordsAdapter verifyBackupMenmonicWordsAdapter;
    private VerifyBackupSelectedMnemonicWordsAdapter verifyBackupSelectedMnemonicWordsAdapter;
    private long walletId;
    private String[] words = null;
    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mnemonic_backup_title);
    }

    @Override
    public void initDatas() {
        walletId = getIntent().getLongExtra("walletId", -1);
        walletMnemonic = getIntent().getStringExtra("walletMnemonic");
        words = walletMnemonic.split("\\s+");
        mnemonicWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            VerifyMnemonicWordTag verifyMnemonicWordTag = new VerifyMnemonicWordTag();
            verifyMnemonicWordTag.setMnemonicWord(words[i]);
            mnemonicWords.add(verifyMnemonicWordTag);
        }
        // 乱序
        Collections.shuffle(mnemonicWords);
        // 未选中单词
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        rvMnemonic.setLayoutManager(layoutManager);
        verifyBackupMenmonicWordsAdapter = new VerifyBackupMnemonicWordsAdapter(R.layout.list_item_mnemoic, mnemonicWords);
        rvMnemonic.setAdapter(verifyBackupMenmonicWordsAdapter);
        // 已选中单词
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(this);
        layoutManager2.setFlexWrap(FlexWrap.WRAP);
        layoutManager2.setAlignItems(AlignItems.STRETCH);
        rvSelected.setLayoutManager(layoutManager2);
        selectedMnemonicWords = new ArrayList<>();
        verifyBackupSelectedMnemonicWordsAdapter = new VerifyBackupSelectedMnemonicWordsAdapter(R.layout.list_item_mnemoic_selected, selectedMnemonicWords);
        rvSelected.setAdapter(verifyBackupSelectedMnemonicWordsAdapter);
    }

    @Override
    public void configViews() {
        verifyBackupMenmonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String mnemonicWord = verifyBackupMenmonicWordsAdapter.getData().get(position).getMnemonicWord();
                if (verifyBackupMenmonicWordsAdapter.setSelection(position)) {
                    verifyBackupSelectedMnemonicWordsAdapter.addData(mnemonicWord);
                }
            }
        });
        verifyBackupSelectedMnemonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!AppUtils.isFastClick()) {
                    return;
                }
                List<VerifyMnemonicWordTag> datas = verifyBackupMenmonicWordsAdapter.getData();
                for (int i = 0; i < datas.size(); i++) {
                    if (TextUtils.equals(datas.get(i).getMnemonicWord(), verifyBackupSelectedMnemonicWordsAdapter.getData().get(position))) {
                        verifyBackupMenmonicWordsAdapter.setUnselected(i);
                        break;
                    }
                }
                verifyBackupSelectedMnemonicWordsAdapter.remove(position);
            }

        });

    }

    @OnClick(R.id.btn_confirm)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                LogUtils.d("VerifyMnemonicBackUp", "Click!!");
                List<String> data = verifyBackupSelectedMnemonicWordsAdapter.getData();
                int size = data.size();
                if (size == 12) {
                    int i = 0;
                    for (i = 0; i < size; i++) {
                        if (!words[i].equals(data.get(i))) {
                            break;
                        }
                    }
                    if(i==12){
                        LogUtils.d("VerifyMnemonicBackUp", "Click!!");
                        WalletDaoUtils.setIsBackup(walletId);
                        setResult(VERIFY_SUCCESS_RESULT, new Intent());
                        finish();
                    } else {
                        ToastUtils.showToast(R.string.verify_backup_failed);
                    }
                } else {
                    ToastUtils.showToast(R.string.verify_backup_failed);
                }
                break;
        }
    }
}
