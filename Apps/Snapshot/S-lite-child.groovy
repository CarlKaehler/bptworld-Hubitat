/**
 *  ****************  Snapshot Lite Child  ****************
 *
 *  Design Usage:
 *  Monitor devices and sensors. Easily get a notification by device, speech and phone.
 *
 *  Copyright 2019 Bryan Turcotte (@bptworld)
 *
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 * 
 *  Unless noted in the code, ALL code contained within this app is mine. You are free to change, ripout, copy, modify or
 *  otherwise use the code in anyway you want. This is a hobby, I'm more than happy to share what I have learned and help
 *  the community grow. Have FUN with it!
 * 
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  2.0.1 - 04/27/20 - Cosmetic changes
 *  2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  1.0.4 - 06/14/19 - Added message to speak when there is no devices out of sync. Will only send push when there is data.
 *  1.0.3 - 06/14/19 - Added even more triggers. 
 *  1.0.2 - 06/13/19 - Added more triggers. Code cleanup.
 *  1.0.1 - 06/13/19 - Fixed push messages
 *  1.0.0 - 06/13/19 - Initial Release
 *
 */

def setVersion(){
    state.name = "Snapshot Lite"
	state.version = "2.0.1"
}

definition(
	name: "Snapshot Lite Child",
	namespace: "BPTWorld",
	author: "Bryan Turcotte",
	description: "Monitor devices and sensors. Easily get a notification by device, speech and phone.",
	category: "Convenience",
	parent: "BPTWorld:Snapshot",
	iconUrl: "",
	iconX2Url: "",
	iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Apps/Snapshot/S-lite-child.groovy",
)

preferences {
	page(name: "pageConfig")
}

def pageConfig() {
	dynamicPage(name: "", title: "", install: true, uninstall: true, refreshInterval:0) {	
    display()
		section("Instructions:", hideable: true, hidden: true) {
			paragraph "Monitor devices and sensors. Easily get a notification by device, speech and phone."	
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Type of Trigger")) {
			
            input "triggerMode", "enum", title: "Select Trigger Type", required: true, submitOnChange: true, options: ["HSM Change","Mode Change", "Motion", "Presence", "Switch On", "Time", "tStat Nest - Heat/Cool", "tStat Other - Heat/Cool"]
            if(triggerMode == "HSM Change") {
                paragraph "<b>Only take a snapshot when HSM changes to...</b>"
                input "hsmSwitch", "enum", title: "App Control Switch", required: true, multiple: true, options: ["armingAway", "armingHome", "armingNight", "armedAway", "armedHome", "armedNight", "disarmed", "allDisarmed"]
            }
            if(triggerMode == "Mode Change") {
                paragraph "<b>Only take a snapshot when Mode changes to...</b>"
                input "modeSwitch", "mode", title: "App Control Switch", required: true, multiple: true
            }
            if(triggerMode == "Motion") {
                paragraph "<b>Only take a snapshot when any Motion Sensor selected becomes active.</b>"
                input "motionSensors", "capability.motionSensor", title: "App Control Switch", required: true, multiple:true
            }
            if(triggerMode == "Presence") {
                paragraph "<b>Only take a snapshot when all Presence Sensore selected are not present.</b>"
                input "presenceSensors", "capability.presenceSensor", title: "App Control Switch", required: true, multiple:true
            }
            if(triggerMode == "Switch On") {
		        paragraph "<b>Only take a snapshot when this switch is turned on</b><br>Recommended to create a virtual device with 'Enable auto off' set to '1s'"
		    	input "onDemandSwitch", "capability.switch", title: "App Control Switch", required: true
            }
            if(triggerMode == "Time") {
		        paragraph "<b>Only take a snapshot at this Time</b><br>Not available yet."
		    	input "timeToRun", "time", title: "App Control Switch", required: true
            }
            if((triggerMode == "tStat Nest - Heat/Cool") || (triggerMode == "tStat Other - Heat/Cool")) {
		        paragraph "<b>Only take a snapshot when this Thermostat is turned on or off (Heat or Cool)</b>"
		    	input "tStatSwitch", "capability.thermostat", title: "App Control Switch", required: true
            }
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Devices to Monitor")) {
			input "switchesOn", "capability.switch", title: "Switches that should be ON", multiple: true, required: false, submitOnChange: true
			input "switchesOff", "capability.switch", title: "Switches that should be OFF", multiple: true, required: false, submitOnChange: true
			input "contactsOpen", "capability.contactSensor", title: "Contact Sensors that should be OPEN", multiple: true, required: false, submitOnChange: true
			input "contactsClosed", "capability.contactSensor", title: "Contact Sensors that should be CLOSED", multiple: true, required: false, submitOnChange: true
			input "locksLocked", "capability.lock", title: "Door Locks that should be LOCKED", multiple: true, required: false, submitOnChange: true
			input "locksUnlocked", "capability.lock", title: "Door Locks that should be UNLOCKED", multiple: true, required: false, submitOnChange: true
			input "temps", "capability.temperatureMeasurement", title: "Temperature Devices", multiple: true, required: false, submitOnChange: true
			if(temps) input "tempHigh", "number", title: "Temp to consider High if over X", required: true, submitOnChange: true
			if(temps) input "tempLow", "number", title: "Temp to consider Low if under X", required: true, submitOnChange: true
        }
		section(getFormat("header-green", "${getImage("Blank")}"+" Options")) {
			input "isDataDevice", "capability.switch", title: "Turn this device on if there are devices to report", submitOnChange: true, required: false, multiple: false
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Notification Options")) {
			paragraph "Receive device notifications on demand with both voice and push options. Great before leaving the house or going to bed."
			paragraph "Each of the following messages will only be spoken if necessary..."
			input(name: "oRandomPre", type: "bool", defaultValue: "false", title: "Random Pre Message?", description: "Random", submitOnChange: "true")
			if(!oRandomPre) input "preMsg", "text", required: true, title: "Pre Message - Single message", defaultValue: "Warning"
			if(oRandomPre) {
				input "preMsg", "text", title: "Random Pre Message - Separate each message with <b>;</b> (semicolon)",  required: true, submitOnChange: "true"
				input(name: "oPreList", type: "bool", defaultValue: "false", title: "Show a list view of the random pre messages?", description: "List View", submitOnChange: "true")
				if(oPreList) {
					def valuesPre = "${preMsg}".split(";")
					listMapPre = ""
    				valuesPre.each { itemPre -> listMapPre += "${itemPre}<br>" }
					paragraph "${listMapPre}"
				}
			}
            paragraph "All switches/devices/contacts/locks/temps in the wrong state will then be spoken"
			input(name: "oRandomPost", type: "bool", defaultValue: "false", title: "Random Post Message?", description: "Random", submitOnChange: "true")
			if(!oRandomPost) input "postMsg", "text", required: true, title: "Post Message - Single message", defaultValue: "This is all I have to say"
			if(oRandomPost) {
				input "postMsg", "text", title: "Random Post Message - Separate each message with <b>;</b> (semicolon)",  required: true, submitOnChange: "true"
				input(name: "oPostList", type: "bool", defaultValue: "false", title: "Show a list view of the random post messages?", description: "List View", submitOnChange: "true")
				if(oPostList) {
					def valuesPost = "${postMsg}".split(";")
					listMapPost = ""
    				valuesPost.each { itemPost -> listMapPost += "${itemPost}<br>" }
					paragraph "${listMapPost}"
				}
			}
            paragraph "If there are no devices to speak, enter something in here to speak instead."
			input(name: "oRandomNothing", type: "bool", defaultValue: "false", title: "Random Nothing to Report Message?", description: "Random", submitOnChange: "true")
			if(!oRandomNothing) input "nothingMsg", "text", required: true, title: "Nothing to Report Message - Single message", defaultValue: "No Devices to report."
			if(oRandomNothing) {
				input "nothingMsg", "text", title: "Random Nothing to Report Message - Separate each message with <b>;</b> (semicolon)",  required: true, submitOnChange: "true"
				input(name: "oNothingList", type: "bool", defaultValue: "false", title: "Show a list view of the random Nothing to Report messages?", description: "List View", submitOnChange: "true")
				if(oNothingList) {
					def valuesNothing = "${nothingMsg}".split(";")
					listMapNothing = ""
    				valuesNothing.each { itemNothing -> listMapNothing += "${itemNothing}<br>" }
					paragraph "${listMapNothing}"
				}
			}
		}	
        section(getFormat("header-green", "${getImage("Blank")}"+" Speech Options")) { 
          	input "speechMode", "enum", required: false, title: "Select Speaker Type", submitOnChange: true,  options: ["Music Player", "Speech Synth"] 
			if (speechMode == "Music Player"){ 
           		input "speaker", "capability.musicPlayer", title: "Choose speaker(s)", required: true, multiple: true, submitOnChange: true
				paragraph "<hr>"
				paragraph "If you are using the 'Echo Speaks' app with your Echo devices then turn this option ON.<br>If you are NOT using the 'Echo Speaks' app then please leave it OFF."
				input(name: "echoSpeaks", type: "bool", defaultValue: "false", title: "Is this an 'echo speaks' app device?", description: "Echo speaks device", submitOnChange: true)
				if(echoSpeaks) input "restoreVolume", "number", title: "Volume to restore speaker to AFTER anouncement", description: "0-100%", required: true, defaultValue: "30"
          	}   
        	if (speechMode == "Speech Synth"){ 
         		input "speaker", "capability.speechSynthesis", title: "Choose speaker(s)", required: true, multiple: true
          	}
			input "sendPushMessage", "capability.notification", title: "Send a Push notification?", multiple: true, required: false
      	}
		section(getFormat("header-green", "${getImage("Blank")}"+" Volume Control Options")) {
			paragraph "NOTE: Not all speakers can use volume controls. If you would like to use volume controls with Echo devices please use the app 'Echo Speaks' and then choose the 'Music Player' option instead of Spech Synth."
			input "volSpeech", "number", title: "Speaker volume for speech", description: "0-100", required: true
			input "volRestore", "number", title: "Restore speaker volume to X after speech", description: "0-100", required: true
           	input "volQuiet", "number", title: "Quiet Time Speaker volume", description: "0-100", required: false, submitOnChange: true
			if(volQuiet) input "QfromTime", "time", title: "Quiet Time Start", required: true
    		if(volQuiet) input "QtoTime", "time", title: "Quiet Time End", required: true
		}
    	if(speechMode){ 
			section(getFormat("header-green", "${getImage("Blank")}"+" Allow messages between what times? (Optional)")) {
        		input "fromTime", "time", title: "From", required: false
        		input "toTime", "time", title: "To", required: false
			}
    	}
		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {label title: "Enter a name for this automation", required: false, submitOnChange: true}
        section() {
            input(name: "logEnable", type: "bool", defaultValue: "true", title: "Enable Debug Logging", description: "Enable extra logging for debugging.")
		}
		display2()
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	if(logEnable) log.debug "Updated with settings: ${settings}"
	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {
	setDefaults()
	if(logEnable) log.debug "In initialize..."
    if(triggerMode == "HSM Change") subscribe(location, "hsmStatus", hsmHandler)
    if(triggerMode == "Mode Change") subscribe(location, "mode", modeHandler)
    if(triggerMode == "Motion") subscribe(motionSensors, "motion.active", priorityCheckHandler)
    if(triggerMode == "Presence") subscribe(presenceSensors, "presence", presenceSensorHandler)
	if(triggerMode == "Switch On") subscribe(onDemandSwitch, "switch.on", priorityCheckHandler)
    if(triggerMode == "Time") schedule(timeToRun, priorityCheckHandler)
    if(triggerMode == "tStat Nest - Heat/Cool") subscribe(tStatSwitch, "thermostatOperatingState.heating", priorityCheckHandler)
    if(triggerMode == "tStat Nest - Heat/Cool") subscribe(tStatSwitch, "thermostatOperatingState.cooling", priorityCheckHandler) 
	if(triggerMode == "tStat Other - Heat/Cool") subscribe(tStatSwitch, "thermostatMode.heat", priorityCheckHandler) 
    if(triggerMode == "tStat Other - Heat/Cool") subscribe(tStatSwitch, "thermostatMode.cool", priorityCheckHandler) 
}

def priorityCheckHandler(evt) {
	if(logEnable) log.debug "In priorityCheckHandler..."
	priorityHandler()
	if(speaker) letsTalk()
    if(state.isData == "yes") { if(sendPushMessage) pushNow() }
    if(logEnable) log.debug "In priorityCheckHandler - No devices to report."
}

def hsmHandler(evt) {
    if(logEnable) log.debug "In hsmHandler..."
	state.hsmStatus = evt.value
	def hsmMatch = hsmSwitch.contains(location.hsmStatus)
    if(hsmMatch) {
        if(logEnable) log.debug "In hsmHandler - MATCH - hsmSwitch: ${hsmSwitch} - Current HSM: ${location.hsmStatus}"
        priorityCheckHandler()
    } else if(logEnable) log.debug "In hsmHandler - NO MATCH - hsmSwitch: ${hsmSwitch} - Current HSM: ${location.hsmStatus}"
}

def modeHandler(evt) {
    if(logEnable) log.debug "In modeHandler..."
	state.modeStatus = evt.value
	def modeMatch = modeSwitch.contains(location.mode)
    if(modeMatch) {
        if(logEnable) log.debug "In modeHandler - MATCH - modeSwitch: ${modeSwitch} - Current Mode: ${location.mode}"
        priorityCheckHandler()
    } else if(logEnable) log.debug "In modeHandler - NO MATCH - modeSwitch: ${modeSwitch} - Current Mode: ${location.mode}"
}

def presenceSensorHandler(evt){
	if(logEnable) log.debug "In presenceSensorHandler..."
    presenceSensors.each { ps ->
        presenceSensorValue = ps.currentValue('presence')
        if(presenceSensorValue == "not present") {
            if(logEnable) log.debug "In presenceSensorHandler - ${ps} is ${presenceSensorValue}."
	    	state.presenceValue = "no"
        } else {
            if(logEnable) log.debug "In presenceSensorHandler - ${ps} is ${presenceSensorValue}!"
            state.presenceValue = "yes"
        }
    }
    if(state.presenceValue == "no") priorityCheckHandler()
}

def priorityHandler(evt){
    state.wrongSwitchesMSG = ""
    state.wrongContactsMSG = ""
    state.wrongLocksMSG = ""
    state.tempLowMSG = ""
    state.tempHighMSG = ""
// Start Priority Switch
	if(switchesOn || switchesOff) {
		if(switchesOn) {
			switchesOn.each { sOn -> 
				def switchName = sOn.displayName
				def switchStatus = sOn.currentValue('switch')
				if(logEnable) log.debug "In priorityHandler - Switch On - ${switchName} - ${switchStatus}"
				if(switchStatus == "off") state.wrongSwitchesMSG += "${switchName}, "
			}
		}
		if(switchesOff) {
			switchesOff.each { sOff -> 
				def switchName = sOff.displayName
				def switchStatus = sOff.currentValue('switch')
				if(logEnable) log.debug "In priorityHandler - Switch Off - ${switchName} - ${switchStatus}"
				if(switchStatus == "on") state.wrongSwitchesMSG += "${switchName}, "
			}
		}
	}
	
// Start Priority Contacts
	if(contactsOpen || contactsClosed) {
		if(contactsOpen) {
			contactsOpen.each { cOpen ->
				def contactName = cOpen.displayName
				def contactStatus = cOpen.currentValue('contact')
				if(logEnable) log.debug "In priorityHandler - Contact Open - ${contactName} - ${contactStatus}"
                if(contactStatus == "closed") state.wrongContactsMSG += "${contactName}, "
			}
		}
		if(contactsClosed) {
			contactsClosed.each { cClosed ->
				def contactName = cClosed.displayName
				def contactStatus = cClosed.currentValue('contact')
				if(logEnable) log.debug "In priorityHandler - Contact Closed - ${contactName} - ${contactStatus}"
				if(contactStatus == "open") state.wrongContactsMSG += "${contactName}, "
			}
		}
	}
		
// Start Priority Locks
	if(locksUnlocked || locksLocked) {
		if(locksUnlocked) {
			locksUnlocked.each { lUnlocked ->
				def lockName = lUnlocked.displayName
				def lockStatus = lUnlocked.currentValue('lock')
				if(logEnable) log.debug "In priorityHandler - Locks Unlocked - ${lockName} - ${lockStatus}"
				if(lockStatus == "locked") state.wrongLocksMSG += "${lockName}, "
			}
		}
		if(locksLocked) {
			locksLocked.each { lLocked ->
				def lockName = lLocked.displayName
				def lockStatus = lLocked.currentValue('lock')
				if(logEnable) log.debug "In priorityHandler - Locks Locked - ${lockName} - ${lockStatus}"
				if(lockStatus == "unlocked") state.wrongLocksMSG += "${lockName}, "
			}
		}
	}
	
// Start Priority Temps
	if(temps) {
		temps.each { device ->
			def tempName = device.displayName
			def tempStatus = device.currentValue('temperature')
			if(logEnable) log.debug "In priorityHandler - Temps - Working on ${tempName} - ${tempStatus}"
			if(tempStatus <= tempLow) state.tempLowMSG += "${tempName}, "
			if(tempStatus >= tempHigh) state.tempHighMSG += "${tempName}, "
		}
	}
    
// Is there Data
    if((state.wrongSwitchesMSG != "") || (state.wrongContactsMSG != "") || (state.wrongLocksMSG != "") || (state.tempLowMSG != "") || (state.tempHighMSG != "")) {
        if(isDataDevice) { isDataDevice.on() }
        state.isData = "yes"
    }
    if((state.wrongSwitchesMSG == "") && (state.wrongContactsMSG == "") && (state.wrongLocksMSG == "") && (state.tempLowMSG == "") && (state.tempHighMSG == "")) {
        if(isDataDevice) { isDataDevice.off() }
        state.isData = "no"
    }
}

def letsTalk() {
	if(logEnable) log.debug "In letsTalk..."
	checkTime()
	checkVol()
	atomicState.randomPause = Math.abs(new Random().nextInt() % 1500) + 400
	if(logEnable) log.debug "In letsTalk - pause: ${atomicState.randomPause}"
	pauseExecution(atomicState.randomPause)
	if(logEnable) log.debug "In letsTalk - continuing"
	if(state.timeBetween == true) {
		messageHandler()
		if(logEnable) log.debug "Speaker in use: ${speaker}"
  		if (speechMode == "Music Player"){ 
    		if(logEnable) log.debug "In letsTalk - Music Player - speaker: ${speaker}, vol: ${state.volume}, msg: ${state.theMsg}"
			if(echoSpeaks) {
				speaker.setVolumeSpeakAndRestore(state.volume, state.theMsg, volRestore)
				if(logEnable) log.debug "In letsTalk - Wow, that's it!"
			}
			if(!echoSpeaks) {
    			if(volSpeech) speaker.setLevel(state.volume)
    			speaker.playTextAndRestore(state.theMsg, volRestore)
				if(logEnable) log.debug "In letsTalk - Wow, that's it!"
			}
  		}   
		if(speechMode == "Speech Synth"){ 
			speechDuration = Math.max(Math.round(state.theMsg.length()/12),2)+3		// Code from @djgutheinz
			atomicState.speechDuration2 = speechDuration * 1000
			if(logEnable) log.debug "In letsTalk - Speech Synth - speaker: ${speaker}, vol: ${state.volume}, msg: ${state.theMsg}"
			if(volSpeech) speaker.setVolume(state.volume)
			speaker.speak(state.theMsg)
			pauseExecution(atomicState.speechDuration2)
			if(volRestore) speaker.setVolume(volRestore)
			if(logEnable) log.debug "In letsTalk - Wow, that's it!"
		}
		log.info "${app.label} - ${state.theMsg}"
	} else if(logEnable) log.debug "In letsTalk - Messages not allowed at this time"
}

def checkTime() {
	if(logEnable) log.debug "In checkTime - ${fromTime} - ${toTime}"
	if((fromTime != null) && (toTime != null)) {
		state.betweenTime = timeOfDayIsBetween(toDateTime(fromTime), toDateTime(toTime), new Date(), location.timeZone)
		if(state.betweenTime) {
			state.timeBetween = true
		} else {
			state.timeBetween = false
		}
  	} else state.timeBetween = true
	if(logEnable) log.debug "In checkTime - timeBetween: ${state.timeBetween}"
}

def checkVol() {
	if(logEnable) log.debug "In checkVol..."
	if(QfromTime) {
		state.quietTime = timeOfDayIsBetween(toDateTime(QfromTime), toDateTime(QtoTime), new Date(), location.timeZone)
    	if(state.quietTime) {
    		state.volume = volQuiet
		} else state.volume = volSpeech
	} else state.volume = volSpeech
	if(logEnable) log.debug "In checkVol - volume: ${state.volume}"
}

def messageHandler() {
	if(logEnable) log.debug "In messageHandler..."
    if(state.isData == "yes") {
	    state.theMsg = ""
    
	    if(oRandomPre) {
	    	def values = "${preMsg}".split(";")
	    	vSize = values.size()
		    count = vSize.toInteger()
    	    def randomKey = new Random().nextInt(count)
		    state.preMsgR = values[randomKey]
		    if(logEnable) log.debug "In messageHandler - Random - vSize: ${vSize}, randomKey: ${randomKey}, Pre Msg: ${state.preMsgR}"
	    } else {
		    state.preMsgR = "${preMsg}"
		    if(logEnable) log.debug "In messageHandler - Static - Pre Msg: ${state.preMsgR}"
	    }
	
	    if(oRandomPost) {
	    	def values = "${postMsg}".split(";")
	    	vSize = values.size()
		    count = vSize.toInteger()
        	def randomKey = new Random().nextInt(count)
		    state.postMsgR = values[randomKey]
		    if(logEnable) log.debug "In messageHandler - Random - vSize: ${vSize}, randomKey: ${randomKey}, Post Msg: ${state.postMsgR}"
	    } else {
		    state.postMsgR = "${postMsg}"
		    if(logEnable) log.debug "In messageHandler - Static - Post Msg: ${state.postMsgR}"
	    }
	
	    state.theMsg = "${state.preMsgR}, "
    
        if(state.wrongSwitchesMSG) { state.theMsg += " Switches: ${state.wrongSwitchesMSG.substring(0, state.wrongSwitchesMSG.length() - 2)}." }
        if(state.wrongDevicesMSG) { state.theMsg += " Devices: ${state.wrongDevicesMSG.substring(0, state.wrongDevicesMSG.length() - 2)}." }
        if(state.wrongContactsMSG) { state.theMsg += " Contacts: ${state.wrongContactsMSG.substring(0, state.wrongContactsMSG.length() - 2)}." }
        if(state.wrongLocksMSG) { state.theMsg += " Locks: ${state.wrongLocksMSG.substring(0, state.wrongLocksMSG.length() - 2)}." }
    
        if(state.tempLowMSG) { state.theMsg += " Temps low: ${state.tempLowMSG.substring(0, state.tempLowMSG.length() - 2)}." }
        if(state.tempHighMSG) { state.theMsg += " Temps high: ${state.tempHighMSG.substring(0, state.tempHighMSG.length() - 2)}." }
    
	    state.theMsg += " ${state.postMsgR}"
	    if(logEnable) log.debug "In messageHandler - theMsg: ${state.theMsg}"
    } else {
        if(oRandomNothing) {
	    	def values = "${nothingMsg}".split(";")
	    	vSize = values.size()
		    count = vSize.toInteger()
        	def randomKey = new Random().nextInt(count)
		    state.theMsg = values[randomKey]
		    if(logEnable) log.debug "In messageHandler - Random - vSize: ${vSize}, randomKey: ${randomKey}, Nothing Msg: ${state.nothingMsgR}"
	    } else {
		    state.theMsg = "${nothingMsg}"
		    if(logEnable) log.debug "In messageHandler - Static - Nothing Msg: ${state.nothingMsgR}"
	    }
    }
}

def pushNow(){
	if(logEnable) log.debug "In pushNow..."
	theMsg = ""
    if(state.wrongSwitchesMSG) {
		state.wrongSwitchPushMap2 = "SWITCHES IN WRONG STATE \n"
		state.wrongSwitchPushMap2 += "${state.wrongSwitchesMSG.substring(0, state.wrongSwitchesMSG.length() - 2)} \n"
		theMsg = "${state.wrongSwitchPushMap2} \n"
    }
    if(state.wrongDevicesMSG) { 
        state.wrongDevicesPushMap2 = "DEVICES IN WRONG STATE \n"
		state.wrongDevicesPushMap2 += "${state.wrongDevicesMSG.substring(0, state.wrongDevicesMSG.length() - 2)} \n"
		theMsg = "${state.wrongDevicesPushMap2} \n"
    }
    if(state.wrongContactsMSG) { 
		state.wrongContactPushMap2 = "CONTACTS IN WRONG STATE \n"
		state.wrongContactPushMap2 += "${state.wrongContactsMSG.substring(0, state.wrongContactsMSG.length() - 2)} \n"
		theMsg += "${state.wrongContactPushMap2} \n"
    }
    if(state.wrongLocksMSG) { 
		state.wrongLockPushMap2 = "LOCKS IN WRONG STATE \n"
		state.wrongLockPushMap2 += "${state.wrongLocksMSG.substring(0, state.wrongLocksMSG.length() - 2)} \n"
		theMsg += "${state.wrongLockPushMap2} \n"
    }
    if(state.tempLowMSG) { 
		state.wrongTempsLowPushMap2 = "TEMPS LOW \n"
		state.wrongTempsLowPushMap2 += "${state.tempLowMSG.substring(0, state.tempLowMSG.length() - 2)} \n"
		theMsg += "${state.wrongTempsLowPushMap2} \n"
    }
    if(state.tempHighMSG) { 
		state.wrongTempsHighPushMap2 = "TEMPS HIGH \n"
		state.wrongTempsHighPushMap2 += "${state.tempHighMSG.substring(0, state.tempHighMSG.length() - 2)} \n"
		theMsg += "${state.wrongTempsHighPushMap2} \n"
    }    
	pushMessage = "${theMsg}"
    if(theMsg) sendPushMessage.deviceNotification(pushMessage)
}

// ********** Normal Stuff **********

def setDefaults(){
	if(logEnable) log.debug "In setDefaults..."
	if(priorityCheckSwitch == null){priorityCheckSwitch = "off"}
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
