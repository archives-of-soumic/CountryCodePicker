package app.soumicslab.countrycodepicker;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CountryCodePickerFragment extends Fragment {

  public static final String TAG = CountryCodePickerFragment.class.getSimpleName();

  private View fragmentRootView;
  private CustomSpinner countrySpinner;
  private Boolean mSpinnerSelectedByUser = true;
  private Boolean isUserTyping = true;
  private CountryCodeFlagAdapter mCountryFlagAdapter;
  public EditText mPhoneEditText;


  AdapterView.OnItemSelectedListener adapterViewOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
      if (!mSpinnerSelectedByUser){
        mSpinnerSelectedByUser = true;
        return;
      }

      mSpinnerSelectedByUser = true;
      isUserTyping = false;
      mPhoneEditText.setText(mCountryFlagAdapter.getItem(i).countryCode);
      mPhoneEditText.setSelection(mPhoneEditText.getText().length());
      /*
      mPhoneEditText.setText(mCountryFlagAdapter.getItem(i).countryCode);
      mPhoneEditText.setSelection(mPhoneEditText.getText().length());
      */
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
  };

  private CustomSpinner.OnSpinnerEventsListener onSpinnerEventsListener = new CustomSpinner.OnSpinnerEventsListener() {
    @Override
    public void onSpinnerOpened() {
      mSpinnerSelectedByUser = true;
    }

    @Override
    public void onSpinnerClosed() { }
  };

  private TextWatcher mTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      if (charSequence.length() <= 3){
        if(isUserTyping) {
          int index = mCountryFlagAdapter.getIndexOfCountryCode(charSequence.toString());
          if (index != -1){
            mSpinnerSelectedByUser = false;
            countrySpinner.setSelection(index, true);
          }
        }else{
          isUserTyping = true;
        }
      }
    }

    @Override
    public void afterTextChanged(Editable editable) { }
  };

  @Nullable
  @org.jetbrains.annotations.Nullable
  @Override
  public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    // return super.onCreateView(inflater, container, savedInstanceState);
    this.fragmentRootView = inflater.inflate(R.layout.demo_fragment, container, false);
    initGui();
    return fragmentRootView;
  }

  private void initGui() {
    this.countrySpinner = fragmentRootView.findViewById(R.id.country_spinner);

    mCountryFlagAdapter = new CountryCodeFlagAdapter(requireActivity(), null, null);

    mPhoneEditText = (EditText) fragmentRootView.findViewById(R.id.phoneNumberEditText);
    mPhoneEditText.addTextChangedListener(mTextWatcher);

    mSpinnerSelectedByUser = true;
    countrySpinner = (CustomSpinner) fragmentRootView.findViewById(R.id.country_spinner);

    countrySpinner.setAdapter(mCountryFlagAdapter);
    countrySpinner.setOnItemSelectedListener(adapterViewOnItemSelectedListener);  // todo: a checkNotNullParameter error, fix it then uncomment it
    countrySpinner.setSpinnerEventsListener(onSpinnerEventsListener);
    // mPhoneEditText.addTextChangedListener(mTextWatcher);  // since country code doesnot belong to the edittext, we can remove this

    // telephony manager
    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
    String simCountryIso = telephonyManager.getSimCountryIso();

    int somePosition = mCountryFlagAdapter.getItemPositionWithSimCountryIso(simCountryIso);
    mPhoneEditText.setText(mCountryFlagAdapter.getItem(somePosition).countryCode);
    countrySpinner.setSelection(somePosition);
  }
}
