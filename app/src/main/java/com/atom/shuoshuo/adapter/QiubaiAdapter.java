package com.atom.shuoshuo.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.qiubai.QiuShiBaiKe;
import com.atom.shuoshuo.listener.OnLoadMoreLisener;
import com.atom.shuoshuo.utils.DateTimeHelper;
import com.atom.shuoshuo.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  11:06
 * modify:
 * version:: V1.0
 * ============================================================
 **/

public class QiubaiAdapter extends RecyclerView.Adapter<QiubaiAdapter.DuanziViewHolder> implements OnLoadMoreLisener {

    private ArrayList<QiuShiBaiKe.Item> mQiubaiLists = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isLoading;
    private RecyclerView mRecyclerView;

    public QiubaiAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public DuanziViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DuanziViewHolder(mInflater.inflate(R.layout.fragment_qiubai_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DuanziViewHolder holder, int position) {
        final QiuShiBaiKe.Item qiubai = mQiubaiLists.get(position);
        if (qiubai.getUser() == null)
            holder.tvAuthor.setText("匿名用户");
        else
            holder.tvAuthor.setText(qiubai.getUser().getLogin());
        holder.tvContent.setText(qiubai.getContent());
        holder.tvTime.setText(DateTimeHelper.getInterval(new Date((long) qiubai.getCreated_at() * 1000)));

//        AnimatorUtils.startSlideInRightAnimator(holder.cardView);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipboard(view, qiubai);
                return true;
            }
        });
    }

    private void copyToClipboard(View view, QiuShiBaiKe.Item qiubai) {
        ClipboardManager manager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", qiubai.getContent());
        manager.setPrimaryClip(clipData);
        SnackbarUtil.showMessage(view, "内容已复制到剪贴板");
    }

    @Override
    public int getItemCount() {
        return mQiubaiLists.size();
    }

    public void addLists(ArrayList<QiuShiBaiKe.Item> list) {
        // 判断返回的数据是否已存在
        if (mQiubaiLists.size() != 0 && list.size() != 0) {
            if (mQiubaiLists.get(0).getContent().equals(list.get(0).getContent()))
                return;
        }

        int size = mQiubaiLists.size();
        if (isLoading) {
            mQiubaiLists.addAll(list);
            notifyItemRangeInserted(size, list.size());
        } else {
            mQiubaiLists.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
            mRecyclerView.scrollToPosition(0);
        }
    }

    public void clearQiubaiList() {
        mQiubaiLists.clear();
    }

    @Override
    public void onLoadStart() {
        if (isLoading)
            return;
        isLoading = true;
    }

    @Override
    public void onLoadFinish() {
        if (!isLoading)
            return;
        isLoading = false;
    }

    private int getLoadingMoreItemPosition() {
        return isLoading ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    static class DuanziViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.duanzi_author)
        TextView tvAuthor;

        @BindView(R.id.duanzi_time)
        TextView tvTime;

        @BindView(R.id.duanzi_content)
        TextView tvContent;

        View cardView;

        public DuanziViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
