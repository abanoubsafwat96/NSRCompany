const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');

admin.initializeApp();


exports.sendNotificationToUsers = functions.database.ref("Sale/{sale_id}").onWrite((change, event) => {
    
    const saleID = event.params.sale_id;
    console.log(saleID)
    
    var userTokensArr = [];
    
    return admin.database().ref("Sale/"+saleID).once('value', snap=>{
       
       var saleDesc = snap.child("description").val()
       var saleTitle = snap.child("title").val()
        
        console.log(saleDesc)
        console.log(saleTitle)
       
        //retriving all user's tokens
        admin.database().ref("Users").once('value', snapshot =>{
              snapshot.forEach(function(childSnapshot) {

                  var key = childSnapshot.key;    
                  // childData will be the actual contents of the child
                  var hasChild = childSnapshot.hasChild("notifications_token")
                   
                  if (hasChild === true){
                      
                          var childData = childSnapshot.child("notifications_token").val(); 
              
                          if(childData!==undefined){
                              
                              userTokensArr.push(childData)
                              console.log("value pushed to userTokensArr")
                            }
                      }
            });
 
            console.log("final array:")
            console.log(userTokensArr)
        
            const payload = {
				notification: {
					title: saleTitle,
					body:saleDesc ,
					icon: "default",
//                    click_action: "com.safwat.abanoub.chatnotifications.NOTIFICATIONACTIVITY" 
                    badge : "1"
				}
			};
            
            if(userTokensArr.length>0){
                 admin.messaging().sendToDevice(userTokensArr, payload)
                            .then(function(response) {
                                console.log("Successfully sent message");
                                return;
                              })
                              .catch(function(error) {
                                console.log("Error sending message:", error);
                              });  
            }else
                console.log("userTokensArr is empty")
        })          
    });
});


exports.sendNotification2ToUsers = functions.database.ref("Products/{product_id}").onWrite((change, event) => {
    
    const productID = event.params.product_id;
    console.log(productID)
    
    var userTokensArr = [];
    
    return admin.database().ref("Products/"+productID).once('value', snap=>{
       
       var productTitle = snap.child("title").val()
       var productPrice = snap.child("price").val()
        
        console.log(productTitle)
        console.log(productPrice)
       
        //retriving all user's tokens
        admin.database().ref("Users").once('value', snapshot =>{
              snapshot.forEach(function(childSnapshot) {

                  var key = childSnapshot.key;    
                  // childData will be the actual contents of the child
                  var hasChild = childSnapshot.hasChild("notifications_token")
                   
                  if (hasChild === true){
                      
                          var childData = childSnapshot.child("notifications_token").val(); 
              
                          if(childData!==undefined){
                              
                              userTokensArr.push(childData)
                              console.log("value pushed to userTokensArr")
                            }
                      }
            });
 
            console.log("final array:")
            console.log(userTokensArr)
        
             const payload = {
                    notification: {
                        title: "منتج جديد: "+productTitle,
                        body:"السعر " +productPrice+" جنية" ,
                        icon: "default",
    //                    click_action: "com.safwat.abanoub.chatnotifications.NOTIFICATIONACTIVITY" 
                        badge : "1"
                    }
                };
                
             if(userTokensArr.length>0){
                 admin.messaging().sendToDevice(userTokensArr, payload)
                            .then(function(response) {
                                console.log("Successfully sent message");
                                return;
                              })
                              .catch(function(error) {
                                console.log("Error sending message:", error);
                              });  
                }else
                    console.log("userTokensArr is empty")
        })          
    });
});


exports.sendNotificationToAdmins = functions.database.ref("Orders/{user_uid}").onWrite((change, event) => {
     
    const userUID = event.params.user_uid;
    console.log(userUID)
    
    var adminsTokensArr = [];
           
    return admin.database().ref("Users/"+userUID).once('value', snap=>{
       
       var userFullName = snap.child("fullname").val()
        
        console.log(userFullName)
           
        //retriving all admin's tokens
        admin.database().ref("Admins").once('value', snapshot=>{
            
                  snapshot.forEach(function(childSnapshot) {

                      var key = childSnapshot.key;
                      // childData will be the actual contents of the child
                      var hasChild = childSnapshot.hasChild("notifications_token")
                      
                      if (hasChild === true){
                          
                          var childData = childSnapshot.child("notifications_token").val();
                          
                          if(childData!==undefined){
                               adminsTokensArr.push(childData)
                               console.log("value pushed to adminsTokensArr");
                            }
                        }
                    });

                    console.log("final array:")
                    console.log(adminsTokensArr)
        
                    const payload = {
                        notification: {
                            title: "لديك طلب جديد",
                            body: userFullName + " ارسل لك طلب جديد" ,
                            icon: "default",
        //                    click_action: "com.safwat.abanoub.chatnotifications.NOTIFICATIONACTIVITY" 
                            badge : "1"
                        }
                    };

                    if(adminsTokensArr.length>0){
                     
                        admin.messaging().sendToDevice(adminsTokensArr, payload)
                                    .then(function(response) {
                                        console.log("Successfully sent message");
                                        return;
                                      })
                                      .catch(function(error) {
                                        console.log("Error sending message:", error);
                                      });  
                            }else
                                console.log("adminsTokenArr is empty")
            })
    });    
});


exports.sendNotification2ToAdmins = functions.database.ref("Rates/{user_uid}").onWrite((change, event) => {
    
    const userUID = event.params.user_uid;
    console.log(userUID)
    
    var adminsTokensArr = [];
           
    return admin.database().ref("Users/"+userUID).once('value', snap=>{
       
       var userFullName = snap.child("fullname").val()
        
        console.log(userFullName)
           
        //retriving all admin's tokens
        admin.database().ref("Admins").once('value', snapshot=>{
            
                  snapshot.forEach(function(childSnapshot) {

                      var key = childSnapshot.key;
                      // childData will be the actual contents of the child
                      var hasChild = childSnapshot.hasChild("notifications_token")
                      
                      if (hasChild === true){
                          
                          var childData = childSnapshot.child("notifications_token").val();
                          
                          if(childData!==undefined){
                               adminsTokensArr.push(childData)
                               console.log("value pushed to adminsTokensArr");
                            }
                        }
                    });

                    console.log("final array:")
                    console.log(adminsTokensArr)
        
                    const payload = {
                        notification: {
                            title: "لديك تقييم جديد",
                            body: userFullName + " ارسل لك تقييم جديد" ,
                            icon: "default",
        //                    click_action: "com.safwat.abanoub.chatnotifications.NOTIFICATIONACTIVITY" 
                            badge : "1"
                        }
                    };

                    if(adminsTokensArr.length>0){
                     
                        admin.messaging().sendToDevice(adminsTokensArr, payload)
                                    .then(function(response) {
                                        console.log("Successfully sent message");
                                        return;
                                      })
                                      .catch(function(error) {
                                        console.log("Error sending message:", error);
                                      });  
                            }else
                                console.log("adminsTokenArr is empty")
            })
    });    
});