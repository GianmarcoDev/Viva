import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:viva/viva.dart';
import 'package:viva_example/lastrecordeddata_shared_preferences.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Event Channel Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Event Channel Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final _omronvivaBcm500Plugin = Viva();

  late StreamSubscription _streamSubscriptionData;
  late StreamSubscription _streamSubscriptionConn;
  late StreamSubscription _streamSubscriptionTra;
  String _currentValue = '';
  String _currentState = '';
  String _currentData = '';
  String _currentScan = '';
  @override
  void initState() {
    super.initState();
    _omronvivaBcm500Plugin.init();
    _startListener();
    _startConnListener();
    _startIsScanListener();
    LastrecordeddataPreferences.init();
  }

  void _startIsScanListener() {
    _streamSubscriptionData =
        _omronvivaBcm500Plugin.streamIsScan.receiveBroadcastStream().listen(
      (event) {
        debugPrint("Received From Native----:  $event\n");
        setState(() {
          _currentScan = event.toString();
        });
      },
    );
  }

  void _startListener() {
    _streamSubscriptionData = _omronvivaBcm500Plugin.streamScan
        .receiveBroadcastStream()
        .listen(_listenStream);
  }

  void _cancelListener() {
    _streamSubscriptionData.cancel();
    setState(() {
      _currentValue = '';
    });
  }

  void _listenStream(value) {
    debugPrint("Received From Native:  $value\n");
    setState(() {
      _currentValue = value.toString();
    });
  }

  void _startConnListener() {
    _streamSubscriptionConn = _omronvivaBcm500Plugin.streamConn
        .receiveBroadcastStream()
        .listen(_listenConnStream);
  }

  void _cancelConnListener() {
    _streamSubscriptionConn.cancel();
    setState(() {
      _currentState = '';
    });
  }

  void _listenConnStream(value) {
    debugPrint("Received Status From Native:  $value\n");
    setState(() {
      _currentState = value.toString();
    });
  }

  void _startTransferListener() {
    _streamSubscriptionTra = _omronvivaBcm500Plugin.streamTransfer
        .receiveBroadcastStream()
        .listen(_listenTransferStream);
  }

  void _cancelTransferListener() {
    _streamSubscriptionTra.cancel();
    setState(() {
      _currentData = '';
    });
  }

  void _listenTransferStream(value) {
    debugPrint("Received Status From Native:  $value\n");
    setState(() {
      _currentData = value.toString();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: ListView(
          children: [
            //Progress bar

            const SizedBox(
              height: 5,
            ),
            Text(_currentScan.toUpperCase(), textAlign: TextAlign.justify),
            const SizedBox(
              height: 50,
            ),
            Text(_currentState.toUpperCase(), textAlign: TextAlign.justify),
            const SizedBox(
              height: 50,
            ),
            // Value in text
            Text(_currentValue.toUpperCase(), textAlign: TextAlign.justify),
            const SizedBox(
              height: 50,
            ),
            Text(_currentData.toUpperCase(), textAlign: TextAlign.justify),
            const SizedBox(
              height: 50,
            ),
            ElevatedButton(
                onPressed: () async {
                  var i = await _omronvivaBcm500Plugin.iScanning();
                  print(i);
                },
                child: const Text('isScan?')),
            ElevatedButton(
                onPressed: () async {
                  var i = await _omronvivaBcm500Plugin.setUserdata(
                      180, 1, '20200601', 1);
                  print(i);
                },
                child: const Text('setdata')),
            ElevatedButton(
                onPressed: () async {
                  var i = await _omronvivaBcm500Plugin.settings();
                  print(i);
                },
                child: const Text('sett')),
            ElevatedButton(
                onPressed: () async {
                  var i = await _omronvivaBcm500Plugin.scan();
                  print(i);
                },
                child: const Text('scan')),
            ElevatedButton(
                onPressed: () async {
                  var i = await _omronvivaBcm500Plugin.setDevicePosition(0);
                  print(i);
                },
                child: const Text('position')),
            ElevatedButton(
                onPressed: () async {
                  var i = await _omronvivaBcm500Plugin.connect();
                  print(i);
                },
                child: const Text('conn')),
            ElevatedButton(
                onPressed: () async {
                  _startTransferListener();
                  var i = await _omronvivaBcm500Plugin.transfer();

                  print(i);
                },
                child: const Text('transfer')),
            //Start Btn
            TextButton(
              onPressed: () => _startListener(),
              child: Text("Start Counter".toUpperCase()),
            ),
            const SizedBox(
              height: 50,
            ),

            //Cancel Btn
            TextButton(
              onPressed: () => _cancelListener(),
              child: Text("Cancel Counter".toUpperCase()),
            ),
          ],
        ),
      ),
    );
  }
}
