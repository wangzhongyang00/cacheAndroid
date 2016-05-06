package maiyatian.livedemo.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import maiyatian.livedemo.IResult;
import maiyatian.livedemo.R;
import maiyatian.livedemo.sk;

/**
 * Created by 王中阳 on 2015/12/16.
 */
public class IResultAdapter extends RecyclerView.Adapter<IResultAdapter.RecyclerHolder> {
    /**
     * 1 inflater
     * 2context
     * 3datas   get set方法
     * 4 构造器
     * 5 data的 get set方法
     */
    private LayoutInflater mInflater;
    private Context mContext;
    private List<IResult> mdatas;
    private IClickListener mCallBack; //信息回调

    public IResultAdapter(Context mContext, IClickListener mCallBack) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mCallBack = mCallBack;
    }


    public void setMdatas(List<IResult> mdatas) {
        this.mdatas = mdatas;
        notifyDataSetChanged(); //观察者模式
    }

    //通过inflater引入item布局文件
    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_simple_weather, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        //从数据源中抽取javabean
        IResult bean = mdatas.get(position);
        if (bean == null) {
            return;
        }
        //通过viewholder设置属性值   TextUtils
        sk sk = bean.getResult().getSk();
        holder.sk.setText(sk.getTime() + "  " + sk.getHumidity() + " " + sk.getWind_direction() + " " + sk.getTemp());
        holder.reason.setText(bean.getReason());
        holder.resultcode.setText(bean.getResultcode());
    }

    @Override
    public int getItemCount() {
        if (mdatas != null && mdatas.size() > 0) {
            return mdatas.size();
        }
        return 0;
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView resultcode, reason, sk;

        public RecyclerHolder(View itemView) {
            super(itemView);
            resultcode = (TextView) itemView.findViewById(R.id.resultcode);
            reason = (TextView) itemView.findViewById(R.id.reason);
            sk = (TextView) itemView.findViewById(R.id.sk);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface IClickListener {
        void onItemClicked(int position);
    }
}
