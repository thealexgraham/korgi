/*
    Interface a NanoKontrol with TouchOSC
*/

NanoTouch {
    /* var <port; */
    /* var <addr; */
    var net, mid, nc, page, myThis;
    var btns;

    /* topBtnsGroup = IdentityDictionary[ */
    /*     \topBt1 -> NKButton(\topBt1, 23), */
    /*     \topBt2 -> NKButton(\topBt2, 24), */
    /*     \topBt3 -> NKButton(\topBt3, 25), */
    /*     \topBt4 -> NKButton(\topBt4, 26), */
    /*     \topBt5 -> NKButton(\topBt5, 27), */
    /*     \topBt6 -> NKButton(\topBt6, 28), */
    /*     \topBt7 -> NKButton(\topBt7, 29), */
    /*     \topBt8 -> NKButton(\topBt8, 30), */
    /*     \topBt9 -> NKButton(\topBt9, 31) */
    /* ]; */

    *new { |addr, outPort, inPort, midiDeviceName, midiPortName|
        "hi".postln;
        addr.postln;
        ^super.new.nanoTouchInit(addr, outPort, inPort, midiDeviceName, midiPortName); // addr, port,);
    }

    nanoTouchInit{ |addr, outPort, inPort, midiDeviceName, midiPortName|
        MIDIClient.init;
        mid = MIDIOut.newByName(midiDeviceName, midiPortName);
        net = NetAddr(addr, outPort);

        nc = NanoKontrol.new;
        page = 1;

        /* btns = IdentityDictionary.new; */

        nc.controllers.keysValuesDo{|key, controller|
            var osc, oscTrue;
            osc = "/"++page++"/"++key.asString;
            oscTrue = "/"++page++"/"++key.asString++"/true";
            osc.postln;

            if (controller.isKindOf(NKButton),
               {
                   this.addButton(osc, controller);
                   // this.addButton(oscTrue, controller);
               },
               {
                   this.addControl(osc, controller);
                   // this.addControl(oscTrue, controller);
               }
            );

            /* makeLabel("/"+page+"/"+key+"/label"); //implement */
        }
    }

    midiToZero { |mn|
        ^this.scale(1, 127, 0, 1, mn);
    }

    scale { |oldMin, oldMax, newMin, newMax, val|
        ^((val - oldMin) * (newMax - newMin) / (oldMax - oldMin)) + newMin;
    }

    addControl{|address, control|
        control.onChanged = { |val|
            var zval;
            zval = this.midiToZero(val);
            zval.postln;
            net.sendMsg(address, zval);
            net.sendMsg(this.toReal(address), zval);
            /* mid.control(16, control.num, zval); */

        }
    }

    addButton{|address, button|
        /* var btn = NTButton.new(false); */
        /* address.postln; */
        /* btns.put(address, btn); */
        /*  */
        /* button.onPress = { |val| */
        /*     btn.pressed; */
        /*     btn.getState.postln; */
        /*     net.sendMsg(address, btn.getState) */
        /* }; */
        /*  */
        /* button.onRelease = { |val| */
        /*     /* net.sendMsg(address, val) */ */
        /* }; */
    }

    toReal{|address| ^address++"/real";}

    free {
        net.free;
    }
}

NTButton {
    var <state;

    *new{|... args|
        ^super.newCopyArgs(*args);
    }

    pressed { state = state != true }

    getState { ^if (state, {127}, {0})  }
}




