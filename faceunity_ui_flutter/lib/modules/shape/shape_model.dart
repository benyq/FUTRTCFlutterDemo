import 'package:json_annotation/json_annotation.dart';
import 'package:faceunity_ui_flutter/util/faceunity_defines.dart';

part 'shape_model.g.dart';

@JsonSerializable()

class ShapeModel {
  String name;
  BeautyShape type;
  double currentValue;
  double defaultValue;
  /// 默认值是否中位数
  bool defaultValueInMiddle;
  /// 是否区分高低端机
  int supportDeviceLevel;

  ShapeModel(this.name, this.type, this.currentValue, this.defaultValue, this.defaultValueInMiddle, this.supportDeviceLevel);

  factory ShapeModel.fromJson(Map<String, dynamic> json) => _$ShapeModelFromJson(json);
  Map<String, dynamic> toJson() => _$ShapeModelToJson(this);
}
