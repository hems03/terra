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
   
    
}

