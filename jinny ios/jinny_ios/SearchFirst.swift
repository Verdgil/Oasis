//
//  SearchFirst.swift
//  jinny_ios
//
//  Created by Verdgil on 07.03.2018.
//  Copyright © 2018 verdgil. All rights reserved.
//

import UIKit
import AVFoundation

extension NSMutableData {
    func appendString(_ string: String) {
        let data = string.data(using: String.Encoding.utf8, allowLossyConversion: false)
        append(data!)
    }
}

class SearchFirst: UIViewController {

    func generateBoundaryString() -> String {
        return "Boundary-\(NSUUID().uuidString)"
    }

    func createBody(parameters: [String: String],
                    boundary: String,
                    data: NSData,
                    mimeType: String,
                    filename: String) -> Data {
        let body = NSMutableData()

        let boundaryPrefix = "--\(boundary)\r\n"

        for (key, value) in parameters {
            body.appendString(boundaryPrefix)
            body.appendString("Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
            body.appendString("\(value)\r\n")
        }

        body.appendString(boundaryPrefix)
        body.appendString("Content-Disposition: form-data; name=\"file\"; filename=\"\(filename)\"\r\n")
        body.appendString("Content-Type: \(mimeType)\r\n\r\n")
        body.append(data as Data)
        body.appendString("\r\n")
        body.appendString("--".appending(boundary.appending("--")))

        return body as Data
    }

    var recordingSession: AVAudioSession!
    var audioRecorder: AVAudioRecorder!

    var button_state = 0 //0 - Запись
                         //1 - Отправить

    @IBOutlet weak var search_label: UILabel!
    @IBOutlet weak var waiting_ring: UIActivityIndicatorView!
    @IBOutlet weak var main_button: UIButton!

    @IBAction func search_click(_ sender: Any) {
        if (button_state == 0) {
            button_state = 1
            main_button.setTitle("Отправить", for: .normal)

            let documents = NSSearchPathForDirectoriesInDomains( FileManager.SearchPathDirectory.documentDirectory,
                    FileManager.SearchPathDomainMask.userDomainMask, true)[0]
            let str =  documents + "/rec.pcm"
            let url = NSURL.fileURL(withPath: str as String)

            let settings = [
                AVFormatIDKey: Int(kAudioFormatLinearPCM),
                AVSampleRateKey: 16000,
                AVNumberOfChannelsKey: 1,
                AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
            ]

            do {
                audioRecorder = try AVAudioRecorder(url: url, settings: settings)
                //audioRecorder.delegate
                audioRecorder.record()

                //recordButton.setTitle("Tap to Stop", for: .normal)
            } catch let err{
                self.search_label.text = "Какая-то ошибка, хз что деалть"
                print(err)
                print(err.localizedDescription)
                //finishRecording(success: false)
            }
        } else {
            button_state = 0
            waiting_ring.alpha = 1
            main_button.setTitle("Запись", for: .normal)
            if(audioRecorder.isRecording) {
                audioRecorder.stop()
                if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {

                    let fileURL = dir.appendingPathComponent(".token")
                    let rec_URL = dir.appendingPathComponent("rec.pcm")

                    //reading
                    do {
                        let token = try String(contentsOf: fileURL, encoding: .utf8)
                        let url_path = "" // подставьте url + "/search_audio"
                        var request = URLRequest(url: URL(string: url_path)!)
                        request.httpMethod = "POST"
                        let params = ["token": token]
                        let boundary = generateBoundaryString()
                        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
                        let data = NSData(contentsOfFile: rec_URL.path)

                        request.httpBody = createBody(parameters: params,
                                boundary: boundary,
                                data: data!,
                                mimeType: "audio/x-pcm",
                                filename: "rec.pcm")
                        request.timeoutInterval = 600
                        print(request.httpBody as Any)
                        let task = URLSession.shared.dataTask(with: request) { data, response, error in
                            guard let data = data, error == nil else {
                                print(error as Any)
                                self.search_label.text = "Какая-то ошибка, хз что деалть 1\n\n" + (error.debugDescription) + "\n\n"
                                self.waiting_ring.alpha = 0
                                return
                            }

                            if let httpStatus = response as? HTTPURLResponse, httpStatus.statusCode != 200 {
                                self.search_label.text = "Какая-то ошибка, хз что деалть 2"// check for http errors
                            }

                            do {
                                if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any] {
                                    if json.keys.contains("author") && json.keys.contains("name") && json.keys.contains("genre") {
                                        var text = ""
                                        text += json["author"] as! String
                                        text += "\n"
                                        text += json["name"] as! String
                                        text += "\n"
                                        text += json["genre"] as! String
                                        print(text)
                                        if json.keys.contains("urls") {
                                            print(json["urls"] as Any)
                                            guard let urls = json["urls"] as? [String: String] else {
                                                //print("\(json["urls"]) couldn't be converted to Dictionary")
                                                return
                                            }
                                            //print(urls)
                                            //print(type(of: urls))
                                            text += "\n"
                                            for url in urls.keys {
                                                //text.customMirror
//                                                let url_str = NSAttributedString
                                                text += url + ": " + (urls[url]!) + "\n"
                                            }
                                        }
                                        self.search_label.text = text
                                        print(self.search_label.text as Any)
                                    } else {
                                        self.search_label.text = String(data: data, encoding: String.Encoding.utf8)
                                    }
                                } else {
                                    self.search_label.text = "000"
                                }
                            } catch let err {
                                self.search_label.text = err.localizedDescription
                            }
                            self.waiting_ring.alpha = 0
                        }
                        task.resume()

                    }
                    catch let error{
                        self.waiting_ring.alpha = 0
                        //self.search_label.text += error.localizedDescription + "\n\n" + error.debugDescription
                        self.search_label.text = "Какая-то ошибка, хз что деалть 3" + (error.localizedDescription) + "\n\n"
                    }
                } else {
                    self.waiting_ring.alpha = 0
                    //self.search_label.text = "Какая-то ошибка, хз что деалть 4"
                }
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        recordingSession = AVAudioSession.sharedInstance()
        search_label.numberOfLines = 0
        do {
            try recordingSession.setCategory(AVAudioSessionCategoryPlayAndRecord)
            try recordingSession.setActive(true)
            recordingSession.requestRecordPermission() { [unowned self] allowed in
                DispatchQueue.main.async {
                    if allowed {
                        //self
                    } else {
                        self.search_label.text = "Права на запись необходимы"
                        //self.search_label.textColor(UIColor(red: 255, green: 0, blue: 0, alpha: 1))
                    }
                }
            }
        } catch {
            // failed to record!
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
