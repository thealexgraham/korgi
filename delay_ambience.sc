//Drifty noodle machine




(
SynthDef(\StereoModDelay, { arg
    //Chorus delay with EQ in the feedback loop
    //for that vintage tape echo mood

	bufferL,
	bufferR,
	time = 1.5,
	fb = 0.8,
	modRate = 5.5,
	modDepth = 0.0005,
	eqFc = 800,
	eqQ = 3;

	var signal, tapPhase, tap, tapPhaseL, tapPhaseR, tapL, tapR;
	var timeMod, eqMod;

	//Drop the input slightly to avoid clicky clipping
	signal = 0.7*In.ar(18, 2);
    signal = signal + LocalIn.ar(2);

    timeMod = LFDNoise3.ar(modRate, modDepth);
    eqMod = LFDNoise3.kr(1, 400);

	tapPhaseL = DelTapWr.ar(bufferL, signal[0]);
	tapL = DelTapRd.ar(bufferL, tapPhaseL, time+LFDNoise3.ar(modRate, modDepth), 2);
	tapL = BBandPass.ar(tapL, eqFc+eqMod, eqQ);

	tapPhaseR = DelTapWr.ar(bufferR, signal[1]);
	tapR = DelTapRd.ar(bufferR, tapPhaseR, time+LFDNoise3.ar(modRate, modDepth), 2);
	tapR = BBandPass.ar(tapR, eqFc+eqMod, eqQ);

	//Restore the output level
	Out.ar(0,[1.4*tapL, 1.4*tapR]);
	LocalOut.ar(fb*[tapR, tapL]);
}).add;

SynthDef(\Filters, { arg
	//Slowly varying bp filter applied across the mix
	//Creates a subtle phasing effect
	cutoffBase = 500,
	cutoffMod = 2000,
	resBase = 0.3,
	lpVol = 0,
	bpVol = 1,
	hpVol = 0,
	notchVol = 0,
	peakVol = 0;

	var signal = In.ar(16,2);

	//Modulated sine wave modulation source
	var modSig = SinOsc.ar(0.05+0.5*LFDNoise3(1), 0, 0.5, 0.5);

	//Two 12dB LPFs for 24db total
	//
	var output = SVF.ar(signal, cutoffBase + (modSig*cutoffMod), resBase, lpVol, bpVol, hpVol, notchVol, peakVol);
	output = 4*SVF.ar(output, cutoffBase + (modSig*cutoffMod), resBase, lpVol, bpVol, hpVol, notchVol, peakVol);

	Out.ar([0,1], output);
	Out.ar([18, 19], output);
}).add;

SynthDef(\ChMach, { arg
	f=440,
	width = 0.5,
	modFreq = 1,
	aTime = 5,
	rTime = 5,
	filter = 2,
	filterQ = 0,
	pan = 0.5;


	 var env = EnvGen.ar(Env([0.01,1,0.01],[aTime, rTime], 'exp'),  doneAction:2);

     var input = Vibrato.ar(VarSaw.ar(f, 0, LFNoise2.kr(1)), 5, 0.1, 0, 0.2, 0.1, 0.7);
	var theSine = SinOsc.ar(f);

//	var theSaw = VarSaw.ar(f*1.5, 0, width);

// Emulate six unsynched LFOs driving six comparators for a multi-pulse chorus

	var oscs = 6;
	var scaler = 1/oscs;

	var lfo1 = LFTri.ar(modFreq*1.51);
	var lfo2 = LFTri.ar(modFreq*1.11);
	var lfo3 = LFTri.ar(modFreq*1.31);
	var lfo4 = LFTri.ar(modFreq*0.71);
	var lfo5 = LFTri.ar(modFreq*0.61);
	var lfo6 = LFTri.ar(modFreq*0.51);

	var comp1 = input > lfo1;
	var comp2 = input > lfo2;
	var comp3 = input > lfo3;
	var comp4 = input > lfo4;
	var comp5 = input > lfo5;
	var comp6 = input > lfo6;

	var output = scaler*(comp1+comp2+comp3+comp4+comp5+comp6);
	//Add a hint of fundamental for body
     output = output+0.001*theSine;

	output = 0.01*LeakDC.ar(output, 0.9995);

	//Doubled Moog with overdrive.
	//Mmmm yeah.
	output = MoogFF.ar(output.tanh*20.0, (f*filter)+LFNoise2.ar(1, 400, 1), LFNoise2.ar(0,3.5, 0));
	output = MoogFF.ar(output*4, f*LFNoise2.kr(0.2, 6, 4), 0.5);

	output = 2*env*output.tanh;
	Out.ar(0, Pan2.ar(output, pan));
	Out.ar(16, Pan2.ar(output, pan));
	Out.ar(18, Pan2.ar(output, pan));

}).add;
Platform.userExtensionDir
);
Quarks.gui
(
	var g=Group.basicNew(s,1);

	var stereoBuffer1L = Buffer.alloc(s, s.sampleRate*3, 1);
	var stereoBuffer1R = Buffer.alloc(s, s.sampleRate*3, 1);

     var monoBuffer1 = Buffer.alloc(s, s.sampleRate*2, 1);

	var rootPitch=36;										// Start on a C

	var stopTranspose = 0;
	var transposeCount = 10;									//Wait a while after transposing to minimise semitone clashes

	//Non-ET pentatonic ratios
	var thisRatio = [0.25, 0.5, 0.75, 1, 1.125, 1.333333, 1.5, 1.6875, 2, 2.25, 2.6666666, 3, 3.375, 4, 5];
	var thisPitch;

	var svf = Synth.tail(g, \Filters);
	var d = Synth.tail(g, \StereoModDelay);

	stereoBuffer1L.zero;
	stereoBuffer1R.zero;
	d.set(\bufferL, stereoBuffer1L);
	d.set(\bufferR, stereoBuffer1R);

	thisPitch = thisRatio.choose*rootPitch.midicps;

	//First few notes have a slow attack and longer interval
	{4.do
		{
		Synth.head(g, \ChMach,
			[\f, thisPitch,
			\width, rrand(0,1),
			\pan, rrand(-1,1),
			\aTime, rrand(5,15),
			\rTime, rrand(7,20),
			\filter, rrand(4,10),
			\filterQ, rrand(0,3.7),
			\modFreq, rrand(0.7,1.5)]);
		  rrand(1,3).wait;
		}
	};

	//Pick a note from the pentatonic scale with somewhat random settings
	//and slowly noodle around the circle of fifths
	{inf.do
		{
		thisPitch = thisRatio.choose*rootPitch.midicps;
		stopTranspose = stopTranspose + 1;
		Synth.head(g, \ChMach,
			[\f, thisPitch,
			\width, rrand(0,1),
			\pan, rrand(-1,1),
			\aTime, rrand(0.01,15),
			\rTime, rrand(7,20),
			\filter, rrand(4,10),
			\filterQ, rrand(0,3.7),
			\modFreq, rrand(0.7,1.5)]);
		if ((0.04.coin) && (stopTranspose > transposeCount)) {
			stopTranspose = 0;
			rootPitch = rootPitch + 7;
			if (rootPitch > 47) {rootPitch = rootPitch - 12};
			rootPitch.postln;
			};
		   rrand(0.1,2).wait;
		   }
	}.fork;
)
