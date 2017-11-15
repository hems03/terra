//
//  Networking.swift
//  Terra
//
//  Created by Shashank Sharma on 9/9/17.
//  Copyright © 2017 Bonfire App. All rights reserved.
//

import Foundation
import Alamofire
import AlamofireObjectMapper

class Networking{
    
    class func getData(completionHandler: @escaping ([SensorData]?, NSError?) -> ()){
       // Alamofire.request(Networking().url! + "/", method: .get, encoding: URLEncoding.default).responseString{
        Alamofire.request("http://ec2-34-229-134-93.compute-1.amazonaws.com/sensordata/", method: .get, encoding: URLEncoding.default).responseArray { (response:
            DataResponse<[SensorData]>) in
            switch response.result {
            case .success(let value):
                completionHandler(value as [SensorData], nil);
            case .failure(let error):
                completionHandler(nil, error as NSError)
            }
        }
    }
    
    class func triggerAction(){
        
        let headers : HTTPHeaders = [
            "Authorization" : "key=AAAAIIMnpQQ:APA91bEggV32WwnSHUGojz7Wc0I8B-fORSyfkmJNbEQpzsoaJcJ8sE2NoF0CZCEYqFZhJ80UXza6RIbH3DtrWCAFbs0BRrAPcnI82kDQ6sFWQBgCHYrRzm6e_2Ur9HQw8WwCOsX5jzrL"
        ]
        
        let parameters = [
            "to" : "dAyJPxjOViw:APA91bGbyXhD6xQVXMkqgONNly0wHosy55vDYSy2PEzocjbRaXscp2xXOJe50I7wAPrl3bIJ4_witbFmSpR_6XQBCSPGTMDurbNFvPeSNTEjVuZTZINXVgiJR9Jaz38O6LpzBbquD555",
            "data" : "TRIGGERED"
        ]
        
    
        Alamofire.request("https://fcm.googleapis.com/fcm/send", method: .post, parameters: parameters, encoding: URLEncoding.default, headers: headers).responseString{ (response) in
                print(response)
        }
    }
   
    class func getWeatherData(completionHandler: @escaping(String) -> ()){
        let headers : HTTPHeaders = [
            "Ocp-Apim-Subscription-Key" : "de2956de741c4daa971abc12bc7fa6aa"
        ]
        
        Alamofire.request("https://earthnetworks.azure-api.net/data/forecasts/v1/daily?subscription-key=&locationtype=latitudelongitude&location=39.9526,-75.1652&verbose=false", method: .get, encoding: URLEncoding.default, headers: headers).responseJSON{
            
            response in
            //print(response)
            //to get status code
            if let status = response.response?.statusCode {
                switch(status){
                case 201:
                    print("example success")
                default:
                    print("error with response status: \(status)")
                }
            }
            //to get JSON return value
            if let result = response.result.value {
                let JSONs = result as! NSDictionary
                let JSONArray = JSONs["dfp"] as! [NSDictionary]
                let JSONdic = JSONArray[0]
                print(JSONdic)
                let forecast = (JSONdic["dd"]!) as! String
                completionHandler(forecast)
            }
            
        }
      //  "https://earthnetworks.azure-api.net/data/forecasts/v1/daily?subscription-key=&locationtype=latitudelongitude&location=39.9526,-75.1652&verbose=false"
    }
    
}

