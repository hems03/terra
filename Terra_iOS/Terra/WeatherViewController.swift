//
//  WeatherViewController.swift
//  Terra
//
//  Created by Shashank Sharma on 9/10/17.
//  Copyright © 2017 Bonfire App. All rights reserved.
//

import UIKit

class WeatherViewController: UIViewController {

    @IBOutlet weak var weatherLabel: UILabel!
    var weatherForecast : String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //Networking.getWeatherData()

        weatherLabel.text = weatherForecast
        // Do any additional setup after loading the view.
    }

    @IBAction func cancel(){
        dismiss(animated: true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func cancelAction(){
        dismiss(animated: true, completion: nil)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
