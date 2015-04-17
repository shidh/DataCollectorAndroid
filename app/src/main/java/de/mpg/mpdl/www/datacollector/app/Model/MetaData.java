package de.mpg.mpdl.www.datacollector.app.Model;

/**
 * Created by allen on 17/04/15.
 */
public class MetaData {

    private Value value;
    // "text": "Item 1"

    // "number": 12

    // GeoLocation
    //            "name": "Amalienstr. 33 D-80799 München",
    //            "longitude": 11.57648,
    //            "latitude": 48.147899

    private String statementUri;
    //"statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/reIk3qTBpcOxyjKb",

    private String typeUri;
    //"typeUri": "http://imeji.org/terms/metadata#geolocation",

    private Object labels;
    //json = "{ \"collectionId\" : \"Qwms6Gs040FBS264\"}";

    //            "language": "en",
    //            "value": "title"



    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String getStatementUri() {
        return statementUri;
    }

    public void setStatementUri(String statementUri) {
        this.statementUri = statementUri;
    }

    public String getTypeUri() {
        return typeUri;
    }

    public void setTypeUri(String typeUri) {
        this.typeUri = typeUri;
    }

    public Object getLabels() {
        return labels;
    }

    public void setLabels(Value labels) {
        this.labels = labels;
    }


    @Override
    public String toString() {
        return "MetaData{" +
                "value='" + value + '\'' +
                ", statementUri='" + statementUri + '\'' +
                ", typeUri='" + typeUri + '\'' +
                ", labels='" + labels + '\'' +
                '}';
    }



/**
[MetaData{value='{text=Item 1}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/8Di1zD7GzAGIgUaY', typeUri='http://imeji.org/terms/metadata#text', labels='[{language=en, value=title}]'},
MetaData{value='{text=Allen}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/B7bSBqhGySbxCM8Q', typeUri='http://imeji.org/terms/metadata#text', labels='[{language=en, value=author}]'},
MetaData{value='{name=Amalienstr. 33 D-80799 München, longitude=11.57648, latitude=48.147899}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/2_FHehws3iKrRDAX', typeUri='http://imeji.org/terms/metadata#geolocation', labels='[{language=en, value=location}]'},
MetaData{value='{number=12.0}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/reIk3qTBpcOxyjKb', typeUri='http://imeji.org/terms/metadata#number', labels='[{language=en, value=accuracy}]'},
MetaData{value='{text=1}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/IqPfT4dfewwmB2aN', typeUri='http://imeji.org/terms/metadata#text', labels='[{language=en, value=deviceID}]'},
MetaData{value='{text=cable}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/bWK8zwhEtNL259a', typeUri='http://imeji.org/terms/metadata#text', labels='[{language=en, value=tags}]'},
MetaData{value='{text=generator}', statementUri='http://dev-faces.mpdl.mpg.de/imeji/statement/bWK8zwhEtNL259a', typeUri='http://imeji.org/terms/metadata#text', labels='[{language=en, value=tags}]'}]




 "metadata": [
 {
 "value": {
 "text": "Item 1"
 },
 "statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/8Di1zD7GzAGIgUaY",
 "typeUri": "http://imeji.org/terms/metadata#text",
 "labels": [
 {
 "language": "en",
 "value": "title"
 }
 ]
 },
 {
 "value": {
 "text": "Allen"
 },
 "statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/B7bSBqhGySbxCM8Q",
 "typeUri": "http://imeji.org/terms/metadata#text",
 "labels": [
 {
 "language": "en",
 "value": "author"
 }
 ]
 },
 {
 "value": {
 "name": "Amalienstr. 33 D-80799 München",
 "longitude": 11.57648,
 "latitude": 48.147899
 },
 "statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/2_FHehws3iKrRDAX",
 "typeUri": "http://imeji.org/terms/metadata#geolocation",
 "labels": [
 {
 "language": "en",
 "value": "location"
 }
 ]
 },
 {
 "value": {
 "number": 12
 },
 "statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/reIk3qTBpcOxyjKb",
 "typeUri": "http://imeji.org/terms/metadata#number",
 "labels": [
 {
 "language": "en",
 "value": "accuracy"
 }
 ]
 },
 {
 "value": {
 "text": "1"
 },
 "statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/IqPfT4dfewwmB2aN",
 "typeUri": "http://imeji.org/terms/metadata#text",
 "labels": [
 {
 "language": "en",
 "value": "deviceID"
 }
 ]
 },
 {
 "value": {
 "text": "cable"
 },
 "statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/bWK8zwhEtNL259a",
 "typeUri": "http://imeji.org/terms/metadata#text",
 "labels": [
 {
 "language": "en",
 "value": "tags"
 }
 ]
 },

 ]
 */

}


