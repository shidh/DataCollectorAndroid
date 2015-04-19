package de.mpg.mpdl.www.datacollector.app.Model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by allen on 01/04/15.
 */
@Table(name = "MetaData")

public class MetaDataLocal extends Model{

    @Column(name = "title")
    private String title;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "accuracy")
    private double accuracy;

    @Column(name = "deviceID")
    private String deviceID;

    @Column(name = "tag")
    private String tag;

    @Column(name = "creator")
    private String creator;

    @Column(name = "whichItem")
    private DataItem whichItem;


/**
    [
    MetaData{typeUri='http://imeji.org/terms/metadata#text', value={text=Item 1}, labels=[{value=title, language=en}]},
    MetaData{typeUri='http://imeji.org/terms/metadata#text', value={text=Allen}, labels=[{value=author, language=en}]},
    MetaData{typeUri='http://imeji.org/terms/metadata#geolocation', value={name=Amalienstr. 33 D-80799 München, longitude=11.57648, latitude=48.147899}, labels=[{value=location, language=en}]},
    MetaData{typeUri='http://imeji.org/terms/metadata#number', value={number=12.0}, labels=[{value=accuracy, language=en}]},
    MetaData{typeUri='http://imeji.org/terms/metadata#text', value={text=1}, labels=[{value=deviceID, language=en}]},
    MetaData{typeUri='http://imeji.org/terms/metadata#text', value={text=cable}, labels=[{value=tags, language=en}]},
    MetaData{typeUri='http://imeji.org/terms/metadata#text', value={text=generator}, labels=[{value=tags, language=en}]}
    ]

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

