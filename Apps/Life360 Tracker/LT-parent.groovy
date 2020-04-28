/**
 *  ****************  Life360 Tracker Parent ****************
 *
 *  Design Usage:
 *  Track your Life360 users. Works with the Life360 with States app.
 *
 *  Copyright 2019-2020 Bryan Turcotte (@bptworld)
 *
 *  This App is free.  If you like and use this app, please be sure to mention it on the Hubitat forums!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  2.0.1 - 04/27/20 - Cosmetic changes
 *  2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  1.0.4 - 08/06/19 - Just changed some wording
 *  1.0.3 - 07/14/19 - Merged code so one app handles both Free and Paid versions. My places now in parent app.
 *  1.0.2 - 07/12/19 - Added code so the main app (life360 tracker) can only be installed once.
 *  1.0.1 - 07/07/19 - Added Lif360 Track Free version
 *  1.0.0 - 07/01/19 - Initial release.
 *
 */

def setVersion(){
    state.name = "Life360 Tracker"
	state.version = "2.0.1"
}

definition(
    name:"Life360 Tracker",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "Track your Life360 users. Works with the Life360 with States app.",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
    singleInstance: true
)

preferences {
    page name: "mainPage", title: "", install: true, uninstall: true
    page name: "myPlacesPage"
} 

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childApps.size()} child apps"
    childApps.each {child ->
    	log.info "Child app: ${child.label}"
    }
}

def mainPage() {
    dynamicPage(name: "mainPage") {
    	installCheck()
		if(state.appInstalled == 'COMPLETE'){
			section("Instructions:", hideable: true, hidden: true) {
				paragraph "<b>Information</b>"
				paragraph "Track your Life360 users. Works with the Life360 with States app."
			}
			section(getFormat("header-green", "${getImage("Blank")}"+" Child Apps")) {
                paragraph "If using the free version of Life360, be sure to fill in 'My Places' before making any child apps."
				app(name: "anyOpenApp", appName: "Life360 Tracker Child", namespace: "BPTWorld", title: "<b>Add a new 'Life360 Tracker' child</b>", multiple: true)
			}
            section(getFormat("header-green", "${getImage("Blank")}"+" My Places - For use with the Free version of Life360 Only")) {
        	    href "myPlacesPage", title: "My Places", description: "Click here to create custom Places"
            }
            
			section(getFormat("header-green", "${getImage("Blank")}"+" General")) {
       			label title: "Enter a name for parent app (optional)", required: false
 			}
			display2()
		}
	}
}

def myPlacesPage() {
    dynamicPage(name: "myPlacesPage", title: "<h2 style='color:#1A77C9;font-weight: bold'>My Places</h2>", nextPage: "mainPage", install: false, uninstall: false) {
        section(getFormat("header-green", "${getImage("Blank")}"+" My Places - For use with the Free version of Life360 Only")) {
            paragraph "Use any service you like to find the Lat and Long for the place you want to save.<br>ie. <a href='https://www.latlong.net' target='_blank'>www.latlong.net</a> or you can use <a href='https://www.life360.com' target='_blank'>www.life360.com</a> to pick a place and a radius.  Then come back here and enter in the options."
            paragraph "Note: Do not create a Place for Home or Work. L360 Tracker will get these directly from L360."

            input "myName01", "text", title: "<b>My Place Name 01</b>", required: false, submitOnChange: true
            if(myName01) {
                input "myLatitude01", "text", title: "My Latitude 01", required: true, width: 4
                input "myLongitude01", "text", title: "My Longitude 01", required: true, width: 4
                input "myRadius01", "text", title: "My Radius 01", required: true, defaultValue: 250, width: 4
            }
            input "myName02", "text", title: "<b>My Place Name 02</b>", required: false, submitOnChange: true
            if(myName02) {
                input "myLatitude02", "text", title: "My Latitude 02", required: true, width: 4
                input "myLongitude02", "text", title: "My Longitude 02", required: true, width: 4
                input "myRadius02", "text", title: "My Radius 02", required: true, defaultValue: 250, width: 4
            }
            input "myName03", "text", title: "<b>My Place Name 03</b>", required: false, submitOnChange: true
            if(myName03) {
                input "myLatitude03", "text", title: "My Latitude 03", required: true, width: 4
                input "myLongitude03", "text", title: "My Longitude 03", required: true, width: 4
                input "myRadius03", "text", title: "My Radius 03", required: true, defaultValue: 250, width: 4
            }
            input "myName04", "text", title: "<b>My Place Name 04</b>", required: false, submitOnChange: true
            if(myName04) {
                input "myLatitude04", "text", title: "My Latitude 04", required: true, width: 4
                input "myLongitude04", "text", title: "My Longitude 04", required: true, width: 4
                input "myRadius04", "text", title: "My Radius 04", required: true, defaultValue: 250, width: 4
            }
            input "myName05", "text", title: "<b>My Place Name 05</b>", required: false, submitOnChange: true
            if(myName05) {
                input "myLatitude05", "text", title: "My Latitude 05", required: true, width: 4
                input "myLongitude05", "text", title: "My Longitude 05", required: true, width: 4
                input "myRadius05", "text", title: "My Radius 05", required: true, defaultValue: 250, width: 4
            }
            input "myName06", "text", title: "<b>My Place Name 06</b>", required: false, submitOnChange: true
            if(myName06) {
                input "myLatitude06", "text", title: "My Latitude 06", required: true, width: 4
                input "myLongitude06", "text", title: "My Longitude 06", required: true, width: 4
                input "myRadius06", "text", title: "My Radius 06", required: true, defaultValue: 250, width: 4
            }
            input "myName07", "text", title: "<b>My Place Name 07</b>", required: false, submitOnChange: true
            if(myName07) {
                input "myLatitude07", "text", title: "My Latitude 07", required: true, width: 4
                input "myLongitude07", "text", title: "My Longitude 07", required: true, width: 4
                input "myRadius07", "text", title: "My Radius 07", required: true, defaultValue: 250, width: 4
            }
            input "myName08", "text", title: "<b>My Place Name 08</b>", required: false, submitOnChange: true
            if(myName08) {
                input "myLatitude08", "text", title: "My Latitude 08", required: true, width: 4
                input "myLongitude08", "text", title: "My Longitude 08", required: true, width: 4
                input "myRadius08", "text", title: "My Radius 08", required: true, defaultValue: 250, width: 4
            }
            input "myName09", "text", title: "<b>My Place Name 09</b>", required: false, submitOnChange: true
            if(myName09) {
                input "myLatitude09", "text", title: "My Latitude 09", required: true, width: 4
                input "myLongitude09", "text", title: "My Longitude 09", required: true, width: 4
                input "myRadius09", "text", title: "My Radius 09", required: true, defaultValue: 250, width: 4
            }
            input "myName10", "text", title: "<b>My Place Name 10</b>", required: false, submitOnChange: true
            if(myName10) {
                input "myLatitude10", "text", title: "My Latitude 10", required: true, width: 4
                input "myLongitude10", "text", title: "My Longitude 10", required: true, width: 4
                input "myRadius10", "text", title: "My Radius 10", required: true, defaultValue: 250, width: 4
            }
        }
    }
}

def installCheck(){
    display()
	state.appInstalled = app.getInstallationState() 
	if(state.appInstalled != 'COMPLETE'){
		section{paragraph "Please hit 'Done' to install '${app.label}' parent app "}
  	}
  	else{
    	log.info "Parent Installed OK"
  	}
}

def getImage(type) {					// Modified from @Stephack Code
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
    if(type == "checkMarkGreen") return "${loc}checkMarkGreen2.png height=30 width=30>"
    if(type == "optionsGreen") return "${loc}options-green.png height=30 width=30>"
    if(type == "optionsRed") return "${loc}options-red.png height=30 width=30>"
    if(type == "instructions") return "${loc}instructions.png height=30 width=30>"
    if(type == "logo") return "${loc}logo.png height=60>"
}

def getFormat(type, myText="") {			// Modified from @Stephack Code   
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "<hr style='background-color:#1A77C9; height: 1px; border: 0;'>"
    if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def display() {
    setVersion()
    getHeaderAndFooter()
    theName = app.label
    if(theName == null || theName == "") theName = "New Child App"
    section (getFormat("title", "${getImage("logo")}" + " ${state.name} - ${theName}")) {
        paragraph "${state.headerMessage}"
		paragraph getFormat("line")
	}
}

def display2() {
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center;font-size:20px;font-weight:bold'>${state.name} - ${state.version}</div>"
        paragraph "${state.footerMessage}"
	}       
}

def getHeaderAndFooter() {
    if(logEnable) log.debug "In getHeaderAndFooter (${state.version})"
    def params = [
	    uri: "https://raw.githubusercontent.com/bptworld/Hubitat/master/info.json",
		requestContentType: "application/json",
		contentType: "application/json",
		timeout: 30
	]
    
    try {
        def result = null
        httpGet(params) { resp ->
            state.headerMessage = resp.data.headerMessage
            state.footerMessage = resp.data.footerMessage
        }
        if(logEnable) log.debug "In getHeaderAndFooter - headerMessage: ${state.headerMessage}"
        if(logEnable) log.debug "In getHeaderAndFooter - footerMessage: ${state.footerMessage}"
    }
    catch (e) {
        state.headerMessage = "<div style='color:#1A77C9'><a href='https://github.com/bptworld/Hubitat' target='_blank'>BPTWorld Apps and Drivers</a></div>"
        state.footerMessage = "<div style='color:#1A77C9;text-align:center'>BPTWorld<br><a href='https://github.com/bptworld/Hubitat' target='_blank'>Find more apps on my Github, just click here!</a><br><a href='https://paypal.me/bptworld' target='_blank'>Paypal</a></div>"
    }
}
