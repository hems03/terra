//
//  ViewController.swift
//  Terra
//
//  Created by Shashank Sharma on 9/9/17.
//  Copyright © 2017 Bonfire App. All rights reserved.
//

import UIKit
import Alamofire

class ViewController: UIViewController {
    @IBOutlet weak var waterLevelView: UIView!
    @IBOutlet weak var sunlightView: UIView!
    @IBOutlet weak var farmView: UIView!
    @IBOutlet weak var weatherForecastView: UIView!
    
    var moistureNumbers : [Double] = []
    var weatherForecast : String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        waterLevelView.layer.cornerRadius = 26
        waterLevelView.clipsToBounds = true
        
        weatherForecastView.layer.cornerRadius = 26
        weatherForecastView.clipsToBounds = true
        
        sunlightView.layer.cornerRadius = 26
        sunlightView.clipsToBounds = true
        
        farmView.layer.cornerRadius = 26
        farmView.clipsToBounds = true
        
        //comment
        moistureNumbers = [0.0, 1.0, 0.0, 1.0, 0.0]
        Networking.getWeatherData(completionHandler: {
            response in
            self.weatherForecast = response
            print(self.weatherForecast)
        })

    }
    
    @IBAction func powerFirebase(_ sender: UIButton) {
        print("Activating sensors")
        //make this get larger
        UIView.setAnimationRepeatAutoreverses(true)
        UIView.animate(withDuration: 1) {
            sender.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
        }
        sender.layer.removeAllAnimations()
        Networking.triggerAction()
    }

    @IBAction func getDataFunction(_ sender: UIButton) {
        UIView.animate(withDuration: 1) {
            sender.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
        }
        print("Getting Data")
        Networking.getData(completionHandler: {
            response, error in
            if error != nil {
                print("error")
            } else {
                if let sensorDataArray = response{
                    for sensorData in sensorDataArray{
                        sensorData.printSensorData()
                        
                        if let sensorId = sensorData.sensor_id{
                            if sensorId == "2"{
                                self.moistureNumbers.append(sensorData.moisture!)
                            }
                        }
                    }
                    print()
                }
            }
        })
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "sensor2Segue"{
            let destination = segue.destination as! TerraZViewController
            destination.moistureNumbers = moistureNumbers
        }
        if segue.identifier == "showWeather"{
            let destination = segue.destination as! WeatherViewController
            destination.weatherForecast = self.weatherForecast
        }
    }
    


}

