package ch.uzh.ifi.hase.soprafs24.constant;

public enum Country {
    Afghanistan("Asia"),
    Albania("Europe"),
    Algeria("Africa"),
    Andorra("Europe"),
    Angola("Africa"),
    AntiguaAndBarbuda("NorthAmerica"),
    Argentina("SouthAmerica"),
    Armenia("Asia"),
    Australia("Oceania"),
    Austria("Europe"),
    Azerbaijan("Asia"),
    Bahamas("NorthAmerica"),
    Bahrain("Asia"),
    Bangladesh("Asia"),
    Barbados("NorthAmerica"),
    Belarus("Europe"),
    Belgium("Europe"),
    Belize("NorthAmerica"),
    Benin("Africa"),
    Bhutan("Asia"),
    Bolivia("SouthAmerica"),
    BosniaAndHerzegovina("Europe"),
    Botswana("Africa"),
    Brazil("SouthAmerica"),
    Brunei("Asia"),
    Bulgaria("Europe"),
    BurkinaFaso("Africa"),
    Burundi("Africa"),
    CaboVerde("Africa"),
    Cambodia("Asia"),
    Cameroon("Africa"),
    Canada("NorthAmerica"),
    CentralAfricanRepublic("Africa"),
    Chad("Africa"),
    Chile("SouthAmerica"),
    China("Asia"),
    Colombia("SouthAmerica"),
    Comoros("Africa"),
    Congo("Africa"),
    CostaRica("NorthAmerica"),
    Croatia("Europe"),
    Cuba("NorthAmerica"),
    Cyprus("Asia"),
    Czechia("Europe"),
    DemocraticPeoplesRepublicOfKorea("Asia"),
    DemocraticRepublicOfTheCongo("Africa"),
    Denmark("Europe"),
    Djibouti("Africa"),
    Dominica("NorthAmerica"),
    DominicanRepublic("NorthAmerica"),
    Ecuador("SouthAmerica"),
    Egypt("Africa"),
    ElSalvador("NorthAmerica"),
    EquatorialGuinea("Africa"),
    Eritrea("Africa"),
    Estonia("Europe"),
    Eswatini("Africa"),
    Ethiopia("Africa"),
    Fiji("Oceania"),
    Finland("Europe"),
    France("Europe"),
    Gabon("Africa"),
    Gambia("Africa"),
    Georgia("Asia"),
    Germany("Europe"),
    Ghana("Africa"),
    Greece("Europe"),
    Grenada("NorthAmerica"),
    Guatemala("NorthAmerica"),
    Guinea("Africa"),
    GuineaBissau("Africa"),
    Guyana("SouthAmerica"),
    Haiti("NorthAmerica"),
    Honduras("NorthAmerica"),
    Hungary("Europe"),
    Iceland("Europe"),
    India("Asia"),
    Indonesia("Asia"),
    Iran("Asia"),
    Iraq("Asia"),
    Ireland("Europe"),
    Israel("Asia"),
    Italy("Europe"),
    IvoryCoast("Africa"),
    Jamaica("NorthAmerica"),
    Japan("Asia"),
    Jordan("Asia"),
    Kazakhstan("Asia"),
    Kenya("Africa"),
    Kiribati("Oceania"),
    Kuwait("Asia"),
    Kyrgyzstan("Asia"),
    Laos("Asia"),
    Latvia("Europe"),
    Lebanon("Asia"),
    Lesotho("Africa"),
    Liberia("Africa"),
    Libya("Africa"),
    Liechtenstein("Europe"),
    Lithuania("Europe"),
    Luxembourg("Europe"),
    Madagascar("Africa"),
    Malawi("Africa"),
    Malaysia("Asia"),
    Maldives("Asia"),
    Mali("Africa"),
    Malta("Europe"),
    MarshallIslands("Oceania"),
    Mauritania("Africa"),
    Mauritius("Africa"),
    Mexico("NorthAmerica"),
    Micronesia("Oceania"),
    Moldova("Europe"),
    Monaco("Europe"),
    Mongolia("Asia"),
    Montenegro("Europe"),
    Morocco("Africa"),
    Mozambique("Africa"),
    Myanmar("Asia"),
    Namibia("Africa"),
    Nauru("Oceania"),
    Nepal("Asia"),
    Netherlands("Europe"),
    NewZealand("Oceania"),
    Nicaragua("NorthAmerica"),
    Niger("Africa"),
    Nigeria("Africa"),
    NorthKorea("Asia"),
    NorthMacedonia("Europe"),
    Norway("Europe"),
    Oman("Asia"),
    Pakistan("Asia"),
    Palau("Oceania"),
    Palestine("Asia"),
    Panama("NorthAmerica"),
    PapuaNewGuinea("Oceania"),
    Paraguay("SouthAmerica"),
    Peru("SouthAmerica"),
    Philippines("Asia"),
    Poland("Europe"),
    Portugal("Europe"),
    Qatar("Asia"),
    Romania("Europe"),
    Russia("Asia"),
    Rwanda("Africa"),
    SaintKittsAndNevis("NorthAmerica"),
    SaintLucia("NorthAmerica"),
    SaintVincentAndTheGrenadines("NorthAmerica"),
    Samoa("Oceania"),
    SanMarino("Europe"),
    SaoTomeAndPrincipe("Africa"),
    SaudiArabia("Asia"),
    Senegal("Africa"),
    Serbia("Europe"),
    Seychelles("Africa"),
    SierraLeone("Africa"),
    Singapore("Asia"),
    Slovakia("Europe"),
    Slovenia("Europe"),
    SolomonIslands("Oceania"),
    Somalia("Africa"),
    SouthAfrica("Africa"),
    SouthKorea("Asia"),
    SouthSudan("Africa"),
    Spain("Europe"),
    SriLanka("Asia"),
    Sudan("Africa"),
    Suriname("SouthAmerica"),
    Sweden("Europe"),
    Switzerland("Europe"),
    Syria("Asia"),
    Tajikistan("Asia"),
    Tanzania("Africa"),
    Thailand("Asia"),
    TimorLeste("Asia"),
    Togo("Africa"),
    Tonga("Oceania"),
    TrinidadAndTobago("NorthAmerica"),
    Tunisia("Africa"),
    Turkey("Asia"),
    Turkmenistan("Asia"),
    Tuvalu("Oceania"),
    Uganda("Africa"),
    Ukraine("Europe"),
    UnitedArabEmirates("Asia"),
    UnitedKingdom("Europe"),
    UnitedStates("NorthAmerica"),
    Uruguay("SouthAmerica"),
    Uzbekistan("Asia"),
    Vanuatu("Oceania"),
    VaticanCity("Europe"),
    Venezuela("SouthAmerica"),
    Vietnam("Asia"),
    Yemen("Asia"),
    Zambia("Africa"),
    Zimbabwe("Africa");

    private final String continent;

    Country(String continent) {
        this.continent = continent;
    }

    public String getContinent() {
        return this.continent;
    }
}
