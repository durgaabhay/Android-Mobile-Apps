package inclass4.group1.beacons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    Discounts discount;
    DiscountInfo discountList;
    private Context context;

    public DiscountAdapter(Context c ,DiscountInfo discountArrayList) {
        this.context = c;
        this.discountList = discountArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discount_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        discount = discountList.getData().get(position);
        holder.discountsData = discount;
        holder.discount.setText(discount.getDiscount());
        holder.price.setText(discount.getPrice());
        holder.product.setText(discount.getName());
        holder.region.setText(discount.getRegion());
        Picasso.with(context).load(discount.getPhoto()).centerCrop().resize(50,50).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return discountList.getData().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView product,discount,price,region;
        ImageView productImage;
        Discounts discountsData;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.discountsData = discountsData;
            product = itemView.findViewById(R.id.textProductName);
            discount = itemView.findViewById(R.id.textDiscount);
            price = itemView.findViewById(R.id.textPrice);
            region = itemView.findViewById(R.id.textRegion);
            productImage = itemView.findViewById(R.id.imageProduct);
        }
    }
}
