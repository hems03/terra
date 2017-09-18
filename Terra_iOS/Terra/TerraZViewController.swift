//
//  TerraZViewController.swift
//  Terra
//
//  Created by Shashank Sharma on 9/9/17.
//  Copyright © 2017 Bonfire App. All rights reserved.
//


//SENSOR 2
//MOISTURE

import UIKit
import Charts

class TerraZViewController: UIViewController {
    
    @IBOutlet weak var moistureChart: LineChartView!
    var moistureNumbers : [Double]!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        moistureChart.backgroundColor = UIColor.white
        //moistureNumbers = [0.0, 1.0, 2.0, 3.0]
        var lineChartEntry = [ChartDataEntry]()
        
        for i in 0..<moistureNumbers.count {
            let value = ChartDataEntry(x: Double(i), y: moistureNumbers[i]) // here we set the X and Y status in a data chart entry
            
            lineChartEntry.append(value)
        }
        let line1 = LineChartDataSet(values: lineChartEntry, label: "Moisture") //Here we convert lineChartEntry to a LineChartDataSet
        
        line1.colors = [NSUIColor.blue] //Sets the colour to blue
        
        let data = LineChartData() //This is the object that will be added to the chart
        
        data.addDataSet(line1) //Adds the line to the dataSet
        
        
        moistureChart.data = data //finally - it adds the chart data to the chart and causes an update
        
        moistureChart.chartDescription?.text = "Moisture for Sensor 2" // Here we set the description for the graph
        
        
        
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func cancelAction(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
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
