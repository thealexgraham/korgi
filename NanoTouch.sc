/*
    Interface a NanoKontrol with TouchOSC
*/

NanoTouch {
    var <port;
    var net, nc, page, myThis;

    *new { |address, port|
        ^super.new.nanoTouchInit(address, port);
    }

    nanoTouchInit{ |address, port|
        net = NetAddr.new(address, port);

        nc = NanoKontrol.new;
        page = 1;

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

    addControl{|address, control|
        control.onChanged = { |val|
            address.postln;
            val.postln;
            net.sendMsg(address, val);
        }
    }

    addButton{|address, button|
        button.onPress = { |val|
            net.sendMsg(address, val)
        };

        button.onRelease = { |val|
            net.sendMsg(address, val)
        };
    }
}
