//
//  ObjectMapper.swift
//  Terra
//
//  Created by Shashank Sharma on 9/9/17.
//  Copyright © 2017 Bonfire App. All rights reserved.
//

import Foundation
import ObjectMapper

class SensorData : Mappable {
    var id : Int?
    var created : String?
    var sensor_id : String?
    var moisture : Double?
    var temperature : Double?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map){
        id <- map["id"]
        created <- map["created"]
        sensor_id <- map["sensor_id"]
        moisture <- map["moisture"]
        temperature <- map["temperature"]
    }
    
    func printSensorData(){
        if let sensor_id = self.sensor_id{
            print("sensor_id: \(sensor_id)")
        }
        if let uniqueId = self.id{
            print("\tunique data id: \(uniqueId)")
        }
        if let created = self.created{
            print("\tcreated: \(created)")
        }
        if let moisture = self.moisture{
            print("\tmoisture: \(moisture)")
        }
        if let temperature = self.temperature{
            print("\ttemperature: \(temperature)")
        }
    }
}
