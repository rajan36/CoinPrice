package com.example.rajan.coinprice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rajan on 4/12/17.
 */

public class CurrencyPriceAdapter extends RecyclerView.Adapter<CurrencyPriceAdapter.CurrencyPriceAdapterViewHolder> {

    private String[] mPriceData;


    public CurrencyPriceAdapter() {

    }


    public class CurrencyPriceAdapterViewHolder extends RecyclerView.ViewHolder {


        public final TextView mCurrencyPriceTextView;

        public CurrencyPriceAdapterViewHolder(View view) {
            super(view);
            mCurrencyPriceTextView = (TextView) view.findViewById(R.id.coin_item_data);
        }
    }


    @Override
    public CurrencyPriceAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.coinprice_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CurrencyPriceAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CurrencyPriceAdapterViewHolder forecastAdapterViewHolder, int position) {
        String coinPrice = mPriceData[position];
        forecastAdapterViewHolder.mCurrencyPriceTextView.setText(coinPrice);
    }


    @Override
    public int getItemCount() {
        if (null == mPriceData) return 0;
        return mPriceData.length;
    }

    public void setPriceData(String[] priceData) {
        mPriceData = priceData;
        notifyDataSetChanged();
    }
}