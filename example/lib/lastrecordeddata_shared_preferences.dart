import 'package:shared_preferences/shared_preferences.dart';

class LastrecordeddataPreferences {
  static SharedPreferences? _lrdPreferences;

  // static const _userEmail = 'email';
  // static const _userPass = 'password';

  static Future init() async {
    _lrdPreferences = await SharedPreferences.getInstance();
  }

  static Future cleanLrd() async {
    print('pulisco lrd');
    if (_lrdPreferences != null) {
      await _lrdPreferences!.clear();
    }
  }
}
