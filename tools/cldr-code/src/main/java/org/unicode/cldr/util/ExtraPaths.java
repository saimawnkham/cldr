package org.unicode.cldr.util;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.unicode.cldr.test.CheckMetazones;

public class ExtraPaths {
    private static final boolean DEBUG = false;

    private static final SupplementalDataInfo supplementalData =
            CLDRConfig.getInstance().getSupplementalDataInfo();

    private static final ImmutableSet<String> casesNominativeOnly =
            ImmutableSet.of(GrammarInfo.GrammaticalFeature.grammaticalCase.getDefault(null));

    /**
     * A set of paths to be added. These are constant across locales, and don't have good fallback
     * values in root. NOTE: if this is changed, you'll need to modify
     * TestPaths.extraPathAllowsNullValue
     */
    private static final Set<String> CONST_EXTRA_PATHS =
            CharUtilities.internImmutableSet(
                    Set.of(
                            // Individual zone overrides
                            "//ldml/dates/timeZoneNames/zone[@type=\"Pacific/Honolulu\"]/short/generic",
                            "//ldml/dates/timeZoneNames/zone[@type=\"Pacific/Honolulu\"]/short/standard",
                            "//ldml/dates/timeZoneNames/zone[@type=\"Pacific/Honolulu\"]/short/daylight",
                            "//ldml/dates/timeZoneNames/zone[@type=\"Europe/Dublin\"]/long/daylight",
                            "//ldml/dates/timeZoneNames/zone[@type=\"Europe/London\"]/long/daylight",
                            "//ldml/dates/timeZoneNames/zone[@type=\"Etc/UTC\"]/long/standard",
                            "//ldml/dates/timeZoneNames/zone[@type=\"Etc/UTC\"]/short/standard",
                            // Person name paths
                            "//ldml/personNames/sampleName[@item=\"nativeG\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeGS\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeGS\"]/nameField[@type=\"surname\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeGGS\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeGGS\"]/nameField[@type=\"given2\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeGGS\"]/nameField[@type=\"surname\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"title\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"given-informal\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"given2\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"surname-prefix\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"surname-core\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"surname2\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"generation\"]",
                            "//ldml/personNames/sampleName[@item=\"nativeFull\"]/nameField[@type=\"credentials\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignG\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignGS\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignGS\"]/nameField[@type=\"surname\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignGGS\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignGGS\"]/nameField[@type=\"given2\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignGGS\"]/nameField[@type=\"surname\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"title\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"given\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"given-informal\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"given2\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"surname-prefix\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"surname-core\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"surname2\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"generation\"]",
                            "//ldml/personNames/sampleName[@item=\"foreignFull\"]/nameField[@type=\"credentials\"]"));

    public static void addConstant(Collection<String> toAddTo) {
        toAddTo.addAll(SingletonHelper.INSTANCE.paths);
    }

    private static class SingletonHelper {
        private static final Singleton INSTANCE = new Singleton();
    }

    private static class Singleton {
        private final Collection<String> paths;
        private Collection<String> pathsTemp;

        Singleton() {
            pathsTemp = new TreeSet<>();
            addPaths(NameType.SCRIPT);
            addPaths(NameType.LANGUAGE);
            addMetazones();
            pathsTemp.addAll(CONST_EXTRA_PATHS);
            paths = ImmutableSet.copyOf(pathsTemp); // preserves order (Sets.copyOf doesn't)
            pathsTemp = null;
        }

        private void addPaths(NameType nameType) {
            StandardCodes.CodeType codeType = nameType.toCodeType();
            StandardCodes sc = StandardCodes.make();
            Set<String> codes = new TreeSet<>(sc.getGoodAvailableCodes(codeType));
            adjustCodeSet(codes, nameType);
            for (String code : codes) {
                pathsTemp.add(nameType.getKeyPath(code));
            }
            addAltPaths(nameType);
        }

        private void adjustCodeSet(Set<String> codes, NameType nameType) {
            if (nameType == NameType.LANGUAGE) {
                codes.remove(LocaleNames.ROOT);
                codes.addAll(
                        List.of(
                                "ar_001", "de_AT", "de_CH", "en_AU", "en_CA", "en_GB", "en_US",
                                "es_419", "es_ES", "es_MX", "fa_AF", "fr_CA", "fr_CH", "frc",
                                "hi_Latn", "lou", "nds_NL", "nl_BE", "pt_BR", "pt_PT", "ro_MD",
                                "sw_CD", "zh_Hans", "zh_Hant"));
            }
        }

        private void addAltPaths(NameType nameType) {
            switch (nameType) {
                case LANGUAGE:
                    addAltPath("en_GB", "short", nameType);
                    addAltPath("en_US", "short", nameType);
                    addAltPath("az", "short", nameType);
                    addAltPath("ckb", "menu", nameType);
                    addAltPath("ckb", "variant", nameType);
                    addAltPath("hi_Latn", "variant", nameType);
                    addAltPath("yue", "menu", nameType);
                    addAltPath("zh", "menu", nameType);
                    addAltPath("zh_Hans", "long", nameType);
                    addAltPath("zh_Hant", "long", nameType);
                    break;
                case SCRIPT:
                    addAltPath("Hans", "stand-alone", nameType);
                    addAltPath("Hant", "stand-alone", nameType);
            }
        }

        private void addAltPath(String code, String alt, NameType nameType) {
            String fullpath = nameType.getKeyPath(code);
            // Insert the @alt= string after the last occurrence of "]"
            StringBuilder fullpathBuf = new StringBuilder(fullpath);
            String altPath =
                    fullpathBuf
                            .insert(fullpathBuf.lastIndexOf("]") + 1, "[@alt=\"" + alt + "\"]")
                            .toString();
            pathsTemp.add(altPath);
        }

        private void addMetazones() {
            for (String zone : supplementalData.getAllMetazones()) {
                final boolean metazoneUsesDST = CheckMetazones.metazoneUsesDST(zone);
                for (String width : new String[] {"long", "short"}) {
                    for (String type : new String[] {"generic", "standard", "daylight"}) {
                        if (metazoneUsesDST || type.equals("standard")) {
                            // Only add /standard for non-DST metazones
                            final String path =
                                    "//ldml/dates/timeZoneNames/metazone[@type=\""
                                            + zone
                                            + "\"]/"
                                            + width
                                            + "/"
                                            + type;
                            pathsTemp.add(path);
                        }
                    }
                }
            }
        }
    }

    public static void addLocaleDependent(
            Set<String> toAddTo, Iterable<String> file, String localeID) {
        SupplementalDataInfo.PluralInfo plurals =
                supplementalData.getPlurals(SupplementalDataInfo.PluralType.cardinal, localeID);
        if (plurals == null && DEBUG) {
            System.err.println(
                    "No "
                            + SupplementalDataInfo.PluralType.cardinal
                            + "  plurals for "
                            + localeID
                            + " in "
                            + supplementalData.getDirectory().getAbsolutePath());
        }
        Set<SupplementalDataInfo.PluralInfo.Count> pluralCounts = Collections.emptySet();
        addUnitPlurals(toAddTo, file, plurals);
        addDayPlurals(toAddTo, localeID);
        addCurrencies(toAddTo, pluralCounts);
        addGrammar(toAddTo, pluralCounts, localeID);
    }

    private static void addUnitPlurals(
            Set<String> toAddTo, Iterable<String> file, SupplementalDataInfo.PluralInfo plurals) {
        if (plurals != null) {
            Set<SupplementalDataInfo.PluralInfo.Count> pluralCounts = plurals.getAdjustedCounts();
            Set<SupplementalDataInfo.PluralInfo.Count> pluralCountsRaw = plurals.getCounts();
            if (pluralCountsRaw.size() != 1) {
                // we get all the root paths with count
                addPluralCounts(toAddTo, pluralCounts, pluralCountsRaw, file);
            }
        }
    }

    private static void addPluralCounts(
            Collection<String> toAddTo,
            final Set<SupplementalDataInfo.PluralInfo.Count> pluralCounts,
            final Set<SupplementalDataInfo.PluralInfo.Count> pluralCountsRaw,
            Iterable<String> file) {
        for (String path : file) {
            String countAttr = "[@count=\"other\"]";
            int countPos = path.indexOf(countAttr);
            if (countPos < 0) {
                continue;
            }
            Set<SupplementalDataInfo.PluralInfo.Count> pluralCountsNeeded =
                    path.startsWith("//ldml/numbers/minimalPairs") ? pluralCountsRaw : pluralCounts;
            if (pluralCountsNeeded.size() > 1) {
                String start = path.substring(0, countPos) + "[@count=\"";
                String end = "\"]" + path.substring(countPos + countAttr.length());
                for (SupplementalDataInfo.PluralInfo.Count count : pluralCounts) {
                    if (count == SupplementalDataInfo.PluralInfo.Count.other) {
                        continue;
                    }
                    toAddTo.add(start + count + end);
                }
            }
        }
    }

    private static void addDayPlurals(Set<String> toAddTo, String localeID) {
        DayPeriodInfo dayPeriods =
                supplementalData.getDayPeriods(DayPeriodInfo.Type.format, localeID);
        if (dayPeriods != null) {
            LinkedHashSet<DayPeriodInfo.DayPeriod> items =
                    new LinkedHashSet<>(dayPeriods.getPeriods());
            items.add(DayPeriodInfo.DayPeriod.am);
            items.add(DayPeriodInfo.DayPeriod.pm);
            for (String context : new String[] {"format", "stand-alone"}) {
                for (String width : new String[] {"narrow", "abbreviated", "wide"}) {
                    for (DayPeriodInfo.DayPeriod dayPeriod : items) {
                        // ldml/dates/calendars/calendar[@type="gregorian"]/dayPeriods/dayPeriodContext[@type="format"]/dayPeriodWidth[@type="wide"]/dayPeriod[@type="am"]
                        toAddTo.add(
                                "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dayPeriods/"
                                        + "dayPeriodContext[@type=\""
                                        + context
                                        + "\"]/dayPeriodWidth[@type=\""
                                        + width
                                        + "\"]/dayPeriod[@type=\""
                                        + dayPeriod
                                        + "\"]");
                    }
                }
            }
        }
    }

    private static void addCurrencies(
            Set<String> toAddTo, Set<SupplementalDataInfo.PluralInfo.Count> pluralCounts) {
        for (String code : supplementalData.getBcp47Keys().getAll("cu")) {
            String currencyCode = code.toUpperCase();
            toAddTo.add(
                    "//ldml/numbers/currencies/currency[@type=\"" + currencyCode + "\"]/symbol");
            toAddTo.add(
                    "//ldml/numbers/currencies/currency[@type=\""
                            + currencyCode
                            + "\"]/displayName");
            if (!pluralCounts.isEmpty()) {
                for (SupplementalDataInfo.PluralInfo.Count count : pluralCounts) {
                    toAddTo.add(
                            "//ldml/numbers/currencies/currency[@type=\""
                                    + currencyCode
                                    + "\"]/displayName[@count=\""
                                    + count.toString()
                                    + "\"]");
                }
            }
        }
    }

    private static void addGrammar(
            Set<String> toAddTo,
            Set<SupplementalDataInfo.PluralInfo.Count> pluralCounts,
            String localeID) {
        GrammarInfo grammarInfo = supplementalData.getGrammarInfo(localeID, true);
        if (grammarInfo != null) {
            if (grammarInfo.hasInfo(GrammarInfo.GrammaticalTarget.nominal)) {
                Collection<String> genders =
                        grammarInfo.get(
                                GrammarInfo.GrammaticalTarget.nominal,
                                GrammarInfo.GrammaticalFeature.grammaticalGender,
                                GrammarInfo.GrammaticalScope.units);
                Collection<String> rawCases =
                        grammarInfo.get(
                                GrammarInfo.GrammaticalTarget.nominal,
                                GrammarInfo.GrammaticalFeature.grammaticalCase,
                                GrammarInfo.GrammaticalScope.units);
                Collection<String> nomCases = rawCases.isEmpty() ? casesNominativeOnly : rawCases;
                // There was code here allowing fewer plurals to be used, but is retracted for now
                // (needs more thorough integration in logical groups, etc.)
                // This note is left for 'blame' to find the old code in case we revive that.

                // TODO use UnitPathType to get paths
                if (!genders.isEmpty()) {
                    for (String unit : GrammarInfo.getUnitsToAddGrammar()) {
                        toAddTo.add(
                                "//ldml/units/unitLength[@type=\"long\"]/unit[@type=\""
                                        + unit
                                        + "\"]/gender");
                    }
                    for (SupplementalDataInfo.PluralInfo.Count plural : pluralCounts) {
                        for (String gender : genders) {
                            for (String case1 : nomCases) {
                                final String grammaticalAttributes =
                                        GrammarInfo.getGrammaticalInfoAttributes(
                                                grammarInfo,
                                                UnitPathType.power,
                                                plural.toString(),
                                                gender,
                                                case1);
                                toAddTo.add(
                                        "//ldml/units/unitLength[@type=\"long\"]/compoundUnit[@type=\"power2\"]/compoundUnitPattern1"
                                                + grammaticalAttributes);
                                toAddTo.add(
                                        "//ldml/units/unitLength[@type=\"long\"]/compoundUnit[@type=\"power3\"]/compoundUnitPattern1"
                                                + grammaticalAttributes);
                            }
                        }
                    }
                    //             <genderMinimalPairs gender="masculine">Der {0} ist
                    // …</genderMinimalPairs>
                    for (String gender : genders) {
                        toAddTo.add(
                                "//ldml/numbers/minimalPairs/genderMinimalPairs[@gender=\""
                                        + gender
                                        + "\"]");
                    }
                }
                if (!rawCases.isEmpty()) {
                    for (String case1 : rawCases) {
                        //          <caseMinimalPairs case="nominative">{0} kostet
                        // €3,50.</caseMinimalPairs>
                        toAddTo.add(
                                "//ldml/numbers/minimalPairs/caseMinimalPairs[@case=\""
                                        + case1
                                        + "\"]");

                        for (SupplementalDataInfo.PluralInfo.Count plural : pluralCounts) {
                            for (String unit : GrammarInfo.getUnitsToAddGrammar()) {
                                toAddTo.add(
                                        "//ldml/units/unitLength[@type=\"long\"]/unit[@type=\""
                                                + unit
                                                + "\"]/unitPattern"
                                                + GrammarInfo.getGrammaticalInfoAttributes(
                                                        grammarInfo,
                                                        UnitPathType.unit,
                                                        plural.toString(),
                                                        null,
                                                        case1));
                            }
                        }
                    }
                }
            }
        }
    }
}
