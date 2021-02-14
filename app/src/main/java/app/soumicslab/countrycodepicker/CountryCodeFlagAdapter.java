package app.soumicslab.countrycodepicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryCodeFlagAdapter extends BaseAdapter {
    public static final String TAG = CountryCodeFlagAdapter.class.getSimpleName();
    private Country[] countries;
    private Context context;

    public CountryCodeFlagAdapter(Context context, String[] blacklist, String[] whitelist){
        this.context = context;
        countries = getAllPhoneCountryCodes(context, blacklist, whitelist);

    }

    @Override
    public int getCount() {
        return countries.length;
    }

    @Override
    public Country getItem(int i) {
        return countries[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(this.context, R.layout.ccp_country_item_normal, null);
        } else {
            view = convertView;
        }
        Country country = this.countries[position];  // the position is wrong!
        Log.e(TAG, "position -> "+position + " country -> "+country.toString());
        TextView countryCodeTextView = view.findViewById(R.id.country_code);
        // countryCodeTextView.setTextSize(context.getResources().getDimension(R.dimen.ridmik_account_flag_font_size));
        countryCodeTextView.setTextColor(Color.BLACK);
        // countryCodeTextView.setText(country.getIsoCodeOrFlagDisplay());
        String selectedCountry = country.name + "(+"+country.countryCode+")";
        countryCodeTextView.setText(selectedCountry);
        return view;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(this.context, R.layout.ccp_country_item, null);
        } else {
            view = convertView;
        }

        LinearLayout llCountryItemRow = view.findViewById(R.id.llCountryItemRow);
        llCountryItemRow.setBackgroundColor(Color.parseColor("#FFFFFF"));

        Country country = this.countries[position];
        TextView labelTextView = view.findViewById(R.id.label);
        labelTextView.setTextColor(Color.BLACK);
        TextView countryCodeTextView = view.findViewById(R.id.country_code);
        labelTextView.setText(country.getCountryNameForDisplay());
        countryCodeTextView.setText(country.getCountryCodeForDisplay());
        return view;
    }

    private static String isoCodeToEmojiFlag(String isoCode) {
        int flagOffset = 127462;
        int asciiOffset = 65;
        int firstChar = Character.codePointAt(isoCode, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(isoCode, 1) - asciiOffset + flagOffset;
        String emoji = new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
        return canShowFlagEmoji(emoji) ? emoji : "";
    }

    public int getIndexOfCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            return -1;
        } else {
            int length = this.countries.length;

            for(int i = 0; i < length; ++i) {
                if (countryCode.equalsIgnoreCase(this.countries[i].countryCode)) {
                    return i;
                }
            }

            return -1;
        }
    }

    private static boolean areFlagsSupported() {
        return Build.VERSION.SDK_INT >= 23;
    }

    private static boolean canShowFlagEmoji(String flagEmoji) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return (new Paint()).hasGlyph(flagEmoji);
    }

    public int getItemPositionWithSimCountryIso(String simCountryIso) {
        String simCountryIsoLowerCase = simCountryIso.toLowerCase();
        for(int i=0; i<countries.length; i++) {
            if(countries[i].isoCode.toLowerCase().equals(simCountryIsoLowerCase)) {
                Log.e(TAG, "match found! "+countries[i].toString() + " simIso = "+simCountryIsoLowerCase);
                return i;
            }
        }
        return 0;
    }

    public static class Country {
        public String name;
        public String isoCode;
        public String countryCode;
        public String emojiFlag;

        Country(String countryCode, String isoCode, String name) {
            this.countryCode = countryCode;
            this.isoCode = isoCode;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Country{" +
                    "name='" + name + '\'' +
                    ", isoCode='" + isoCode + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    ", emojiFlag='" + emojiFlag + '\'' +
                    '}';
        }

        private String getCountryCodeForDisplay() {
            return String.format("+%s", this.countryCode);
        }

        private String getIsoCodeOrFlagDisplay() {
            return (this.emojiFlag != null ? this.emojiFlag : this.isoCode);
        }

        private String getCountryNameForDisplay() {
            return this.emojiFlag == null ? this.name : String.format("%s %s", this.emojiFlag, this.name);
        }

    }

    private static Country[] getAllPhoneCountryCodes(Context context, String[] blacklist, String[] whitelist) {
        String[] resources = context.getResources().getStringArray(R.array.com_ridmik_account_phone_country_codes);
        List<Country> phoneCountryCodeList = new ArrayList<>();
        Set<String> clientWhitelisted = whitelist != null ? new HashSet(Arrays.asList(whitelist)) : null;
        Set<String> clientBlacklisted = blacklist != null && blacklist.length > 0 ? new HashSet(Arrays.asList(blacklist)) : new HashSet();

        boolean showEmoji = areFlagsSupported() && canShowFlagEmoji(isoCodeToEmojiFlag("BD"));

        for (String resource : resources) {
            String[] components = resource.split(":", 3);
            if (!clientBlacklisted.contains(components[1]) && (clientWhitelisted == null || clientWhitelisted.contains(components[1]))) {
                Country country = new Country(components[0], components[1], components[2]);
                if (showEmoji)
                    country.emojiFlag = isoCodeToEmojiFlag(country.isoCode);
                phoneCountryCodeList.add(country);
            }
        }

        final Collator collator = Collator.getInstance(Resources.getSystem().getConfiguration().locale);
        collator.setStrength(0);
        Collections.sort(phoneCountryCodeList, new Comparator<Country>() {
            public int compare(Country c1, Country c2) {
                return collator.compare(c1.name, c2.name);
            }
        });

        Country bd = new Country("880", "BD", "Bangladesh");
        bd.emojiFlag = isoCodeToEmojiFlag(bd.isoCode);
        phoneCountryCodeList.add(0, bd);

        Country[] phoneCountryCodes = new Country[phoneCountryCodeList.size()];

        phoneCountryCodeList.toArray(phoneCountryCodes);
        return phoneCountryCodes;
    }

}
