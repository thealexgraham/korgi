s = Server.start;
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

(
    /* ~ip = "192.168.1.3"; */
    ~ip = "172.20.10.6";
    ~portOut = 9000;
    ~portIn  = 8000;
    t = NanoTouch(~ip, 9000, 8000, "IAC Driver", "korgi");
/* c   t = NanoTouch(~ip, ~portOut); */
)
Quarks.gui
(
    g = NanoGUI.new;
    TabbedView
)

(
    /* n = NanoKontrol.new */
    /* var page */
    /* page = "1" */
    m = MIDIClient.init;
    MIDIClient.sources;

    m = MIDIOut.newByName("IAC Driver", "korgi");

    m.postln;
    MIDIEndPoint("nanoKONTROL", "SLIDER/KNOB").class;
    /* MIDIClient.sources.postln; */
    a.MIDIClient.sources.select({|item, i| item.device == "nanoKONTROL" }).at(0).postln
    a = MIDIClient.sources;
    c = MIDIClient.sources.at(3);
    uid.postln;
    t = NanoTouch(~ip, 9000, "IAC Driver", "korgi");
    /* n = NetAddr("172.20.10.5", 9000); */
    n = NetAddr("192.168.1.3", 9000);
    n.postln;
    n.sendMsg("/1/fader1",0.5);
    ControlRate.ir.postln;

    { ControlRate.ir.poll }.play;
 /*     n.controllers.keyValuesDo(|key, controller| */
/*         if (controller.isKindOf(NKButton) */
/*                               ,{t.addbutton("/"+page+"/"+key, controller);} */
/*                               ,{t.controller("/"+page+"/"+key, controller);} */
/*         ) */
/*     }; */
/* ( */
/*  */
/*     n.fader1.onChanged = {|val| */
/*         "fader 1 changed" ++ val.postln; */
/*         val.postln */
/*     }; */
/*  */
/*     n.topBt1.onPress   = {|val| "top button 1 pressed".postln; val.postln }; */
/*  */
/*     n.topBt1.onRelease = {|val| "top button 1 released".postln; val.postln }; */
/*  */
/*     n.knob1.onChanged  = {|val| "knob 1 changed".postln; val.postln }; */
/* ) */
/* ( */
/*     n.faders.do{|fader, i| */
/*         fader.onChanged = {|val| ("Fader"+(i+1)).postln; val.postln }; */
/*  */
/*     }; */
)w
