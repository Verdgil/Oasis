//
//  MoreVC.swift
//  jinny_ios
//
//  Created by Verdgil on 08.04.2018.
//  Copyright © 2018 verdgil. All rights reserved.
//

import UIKit

class MoreVC: UIViewController {

    @IBOutlet weak var logout_button: UIButton!
    
    @IBAction func logout_click(_ sender: Any) {
        if let file_url = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {

            let path = file_url.appendingPathComponent(".token")
            let fileManager = FileManager.default

// Delete 'hello.swift' file

            do {
                try fileManager.removeItem(atPath: path.path)
                let storyboard = UIStoryboard(name: "Login_screen", bundle: nil)
                //let controller = storyboard.instantiateViewController(withIdentifier: "Search") as UIViewController
                let controller = storyboard.instantiateInitialViewController()!
                self.present(controller, animated: true, completion: nil)
            } catch let error as NSError {
                print("Ooops! Something went wrong: \(error)")

            }
        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
