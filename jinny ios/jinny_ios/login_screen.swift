//
//  login_screen.swift
//  jinny_ios
//
//  Created by Verdgil on 07.03.2018.
//  Copyright © 2018 verdgil. All rights reserved.
//

import UIKit

class login_screen: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var log_reg_choise: UISegmentedControl!
    @IBOutlet weak var password_field: UITextField!
    @IBOutlet weak var login_field: UITextField!
    @IBOutlet weak var login_button: UIButton!
    @IBOutlet weak var err_label: UILabel!
    @IBOutlet weak var wait_ring: UIActivityIndicatorView!


    @IBAction func login_click(_ sender: Any) {
        wait_ring.alpha = 1
        err_label.text = ""
        let login = login_field.text
        let password = password_field.text
        let log_reg = log_reg_choise.selectedSegmentIndex
        if login!.isEmpty || password!.isEmpty {
            //login_field.backgroundColor = UIColor(red: 255, green: 0, blue: 0, alpha: 1)
            //login_field
            err_label.text = "Логин и пароль не могут быть пустыми"
            wait_ring.alpha = 0
        } else {
            var url_path = "" // Подставьте URL
            if log_reg == 0 {
                url_path += "login"
            } else {
                url_path += "register"
            }

            let url = URL(string: url_path)!
            var request = URLRequest(url: url)
            request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
            request.httpMethod = "POST"
            let post_string = "login=" + login! + "&password=" + password! + "&lang=ru_RU.UTF-8"
            request.httpBody = post_string.data(using: .utf8)
            let task = URLSession.shared.dataTask(with: request) { data, response, error in
                guard let data = data, error == nil else {// check for fundamental networking error
                    self.err_label.text = "Что-то не так, проверьте соединение с интернетом"
                    self.wait_ring.alpha = 0
                    return
                }

                if let httpStatus = response as? HTTPURLResponse, httpStatus.statusCode != 200 {           // check for http errors
                    self.err_label.text = "Что-то с сервером, подождите"
                    self.wait_ring.alpha = 0
                }

                do {
                    if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any] {
                        if json.keys.contains("token") {
                            let token_file_name = ".token"
                            let token = String(describing: json["token"]!)
                            if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {

                                let path = dir.appendingPathComponent(token_file_name)

                                //writing
                                do {
                                    try token.write(to: path, atomically: false, encoding: String.Encoding.utf8)
                                }
                                catch {/* error handling here */}

                            }
                            self.wait_ring.alpha = 0
                            let storyboard = UIStoryboard(name: "Search", bundle: nil)
                            //let controller = storyboard.instantiateViewController(withIdentifier: "Search") as UIViewController
                            let controller = storyboard.instantiateInitialViewController()!
                            self.present(controller, animated: true, completion: nil)

                        } else {
                            self.err_label.text = String(describing: json.first!.value) //TODO: FIX
                            self.wait_ring.alpha = 0
                        }
                    }
                } catch let err{
                    print(err)
                    self.err_label.text = "Что-то не так, проверьте соединение с интернетом"
                    self.wait_ring.alpha = 0
                }
            }
            task.resume()
        }
    }

    @IBAction func log_reg_change(_ sender: Any) {
        if log_reg_choise.selectedSegmentIndex == 0 {
            login_button.setTitle("Логин", for: .normal)
        } else {
            login_button.setTitle("Регистрация", for: .normal)
        }
    }

    func textFieldShouldReturn(_ login_field: UITextField) -> Bool {
        self.view.endEditing(true)
        self.login_button.sendActions(for: .touchUpInside)
        return true
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        login_field.delegate = self
        password_field.delegate = self
        let path = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] as String
        let url = NSURL(fileURLWithPath: path)
        if let pathComponent = url.appendingPathComponent(".token") {
            let filePath = pathComponent.path
            let fileManager = FileManager.default
            if fileManager.fileExists(atPath: filePath) {
                DispatchQueue.main.asyncAfter(deadline: .now(), execute: {
                    let storyboard = UIStoryboard(name: "Search", bundle: nil)
                    let controller = storyboard.instantiateInitialViewController()!
                    self.present(controller, animated: true, completion: nil)
                })

            }
        }

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
