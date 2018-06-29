
(

    fadergroup= IdentityDictionary[
        \fader1 -> NKController(\fader1, 2),
        \fader2 -> NKController(\fader2, 3),
        \fader3 -> NKController(\fader3, 4),
        \fader4 -> NKController(\fader4, 5),
        \fader5 -> NKController(\fader5, 6),
        \fader6 -> NKController(\fader6, 8),
        \fader7 -> NKController(\fader7, 9),
        \fader8 -> NKController(\fader8, 12),
        \fader9 -> NKController(\fader9, 13)
        ];
)
"asjdka"+\test.asSymbol+"hiii".postln;
(
    n = NanoKontrol.new
    var page
    page = "1"
    t = TouchOSC.new(port)

    n.controllers.keyValuesDo(|key, controller|
        if (controller.isKindOf(NKButton)
                              ,{t.addbutton("/"+page+"/"+key, controller);}
                              ,{t.controller("/"+page+"/"+key, controller);}
        )
    };
(

    n.fader1.onChanged = {|val|
        "fader 1 changed" ++ val.postln;
        val.postln
    };

    n.topBt1.onPress   = {|val| "top button 1 pressed".postln; val.postln };

    n.topBt1.onRelease = {|val| "top button 1 released".postln; val.postln };

    n.knob1.onChanged  = {|val| "knob 1 changed".postln; val.postln };
)
(
    n.faders.do{|fader, i|
        fader.onChanged = {|val| ("Fader"+(i+1)).postln; val.postln };

    };
)
