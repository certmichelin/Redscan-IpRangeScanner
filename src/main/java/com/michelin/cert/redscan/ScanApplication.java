/*
 * Copyright 2021 Michelin CERT (https://cert.michelin.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michelin.cert.redscan;

import com.michelin.cert.redscan.utils.models.IpRange;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RedScan scanner main class.
 *
 * @author Maxime ESCOURBIAC
 * @author Sylvain VAISSIER
 * @author Maxence SCHMITT
 */
@SpringBootApplication
public class ScanApplication {

  private final RabbitTemplate rabbitTemplate;

  /**
   * Constructor to init rabbit template. Only required if pushing data to queues
   *
   * @param rabbitTemplate Rabbit template.
   */
  public ScanApplication(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  /**
   * RedScan Main methods.
   *
   * @param args Application arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(ScanApplication.class, args);
  }

  /**
   * Message executor.
   *
   * @param message Message received.
   */
  @RabbitListener(queues = {RabbitMqConfig.QUEUE_IPRANGES})
  public void receiveMessage(String message) {
    IpRange ipRange = new IpRange();
    try {
      ipRange.fromJson(message);
      LogManager.getLogger(ScanApplication.class).info(String.format("Blah : %s", ipRange.getCidr()));

      //ToDo: Get all IPs from CIDR then scan.
    } catch (Exception ex) {
      LogManager.getLogger(ScanApplication.class).error(String.format("General Exception : %s", ex.getMessage()));
    }
  }

  private boolean ping(String ip) {
    LogManager.getLogger(ScanApplication.class).info(String.format("Sending ping request to : %s", ip));
    boolean result = false;
    try {
      InetAddress inet = InetAddress.getByName(ip);
      result = inet.isReachable(5000);
    } catch (UnknownHostException ex) {
      LogManager.getLogger(ScanApplication.class).error(String.format("Unkonwn Host Exception : %s", ex.getMessage()));
    } catch (IOException ex) {
      LogManager.getLogger(ScanApplication.class).error(String.format("Oing IOException : %s", ex.getMessage()));
    }
    return result;
  }
}
