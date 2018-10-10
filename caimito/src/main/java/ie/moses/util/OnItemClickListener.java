package ie.moses.util;

import android.support.annotation.IntRange;

public interface OnItemClickListener {

    void onItemClicked(@IntRange(from = 0) int position);

}
