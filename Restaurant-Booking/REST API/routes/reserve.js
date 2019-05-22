const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const checkAuth = require('../auth/check-auth');
const dateFormat = require('dateformat');

const readyStatus = 'READY';
const reservedStatus = 'RESERVED';
const inqueueStatus = 'INQUEUE';

const twilio = require('twilio');
const accountSid = '';
const authToken = '';
const client = new twilio(accountSid, authToken);

const Reservation = require('../models/reservation');
const Customer = require('../models/users');
const Feedback = require('../models/feedback');

var userStatus = '';

router.get('/readFreeTables', checkAuth, (req,res,next) => {
    Reservation.find({status : readyStatus,tableType:'ALACARTE'}).exec()
        .then( readyTables => {
            console.log('List if available tables' , readyTables);
            res.status(200).json({
                tableCount : readyTables.length,
                tableData : readyTables
            })
        }).catch( err => {
            res.status(500).json({
                message : 'Error reading free data',
                error : err
            })
    });
});

router.get('/alacarteTableStatus', checkAuth, (req,res,next) => {

    Reservation.find({tableType : 'ALACARTE'}).exec()
        .then( result => {
            console.log('List if available tables' , result);
            res.status(200).json({result});
        }).catch( err => {
        res.status(500).json({
            message : 'Error reading table data',
            error : err
        })
    });
});

router.get('/reservationTableStatus',checkAuth, (req,res,next) => {
   Reservation.find({tableType : 'RESERVATION'}).exec()
       .then( result => {
           console.log('List of reserved tables' , result);
           res.status(200).json({result});
       })
       .catch( err => {
           res.status(500).json({
               message : 'Error reading reserved tables',
               error: err
           })
       });
});

//lists out the customers who are in queue
router.get('/inqueue',checkAuth,(req,res,next) => {
    console.log('Checking customers inqueue');
    Customer.find({tableStatus:inqueueStatus}).exec()
        .then( result => {
            console.log('inqueue customers',result)
            res.status(200).json({result});
        }).catch(err => {
            res.status(500).json({
                message : 'Error loading customers inqueue',
                error : err
            });
    });
});

router.post('/inbound', (req,res,next) => {
    console.log('Inbound message is getting in' , req.body.Body);
    let body = req.body.Body;
    let from = req.body.From;
    let to = req.body.To;
    let reserveTime = new Date(body);
    let seats = body;
    Customer.find({phoneNumber : from}).exec()
        .then( foundCustomer => {
            console.log('Does the customer exists ?', foundCustomer);
            console.log('table status ' + foundCustomer[0].tableStatus);
            if(foundCustomer.length == 1) {
                if (body === 'Reserve') {
                    client.messages.create({
                        to :`${from}`,
                        from :`${to}`,
                        body : 'How many people are you making a reservation for?'
                    })
                }
                else if(!isNaN(body)){//checking if body entered value is a number
                    Customer.updateOne({phoneNumber:req.body.from}, {$set: {noOfSeats: body}}).exec();
                    client.messages.create({
                        to :`${from}`,
                        from :`${to}`,
                        body : 'Time of reservation? Specify in this format: 2018-10-08 13:35'
                    })
                }else if(body === 'Status') {
                    //user is checking for his/her status
                    Customer.find({phoneNumber: from}).then(result => {
                        userStatus = result[0].tableStatus;
                        console.log('User looking for a status ', userStatus);
                        client.messages.create({
                            to: `${from}`,
                            from: `${to}`,
                            body: 'Your status is "' + userStatus + '" . Average waiting time will be 15-30 minutes.Thank you'
                        });
                    }).catch(err => {
                        client.messages.create({
                            to: `${from}`,
                            from: `${to}`,
                            body: 'We are experiencing difficulties at the moment. Please try again in a while'
                        });
                    });
                }
                else if(body === 'Yes' || body === 'No'){
                    //insert user feedback into database
                    const feedback = new Feedback({
                        _id : mongoose.Types.ObjectId(),
                        phoneNumber : from,
                        feedback : body
                    });
                    feedback.save().then( result => {
                        console.log('User feedback updated');
                        res.status(200).json({result});
                    }).catch( err => {
                        console.log('error saving user feedback');
                        res.status(500).json({err})
                    });
                }else if (reserveTime instanceof Date){
                    console.log('yes, user entered reserveTime correctly' , reserveTime);
                    //add new tables only for phone reservations
                    Reservation.find({status: 'READY', tableType:'RESERVATION'}).exec()
                        .then( foundTable => {
                            if(foundTable.length >= 1){
                                Reservation.updateOne({_id:foundTable[0]._id} , {$set : {userName: foundCustomer.userName, status:'RESERVED', bookingDate: reserveTime, phoneNumber : from}}).exec();
                                Customer.updateOne({phoneNumber:from},{$set : {tableStatus:'RESERVED'}}).exec();
                                client.messages.create({
                                    to : `${from}`,
                                    from : `${to}`,
                                    body : 'Your reservation has been made. You will also receive a reminder.Thanks!'
                                })
                            }else{
                                Customer.updateOne({phoneNumber:from}, {$set : {tableStatus : 'INQUEUE'}}).exec()
                                    .then(result => {
                                        client.messages.create({
                                            to : `${from}`,
                                            from : `${to}`,
                                            body : 'Welcome"' + req.body.userName + '"to Awesome Food. Thank you for your patience. Once a table is ready you will make a reservation'
                                        });
                                        res.status(200).json({
                                            message : 'Putting user in queue for reservation',
                                            customerDetails : {result}
                                        })
                                    }).catch(err => {
                                        res.status(500).json({
                                            message : 'Error adding reservation for user',
                                            error : err
                                        })
                                })
                            }
                        }).catch(err => {
                            res.status(500).json({
                               message : 'Error finding table for reservation',
                                error : err
                            });
                    });
                }else {
                        client.messages.create({
                                to :`${from}`,
                                from :`${to}`,
                                body : 'Do you wish to make a reservation? Please send message Reserve to the same number.You can also send Status to check your waiting time'
                        });
                }
            }else{
                //create new customer first
                const newCustomer = new Customer({
                    _id: mongoose.Types.ObjectId(),
                    phoneNumber: req.body.From
                });
                newCustomer.save();
                client.messages.create({
                    to :`${from}`,
                    from :`${to}`,
                    body : 'Welcome new customer. Please type Reserve to start a reservation for you!We have 2,3,4,5,6 seats.'
                })
            }
        });
});

router.post('/createTables', (req,res,next) => {
   const newTable = new Reservation({
     _id : mongoose.Types.ObjectId(),
     tableNumber : req.body.tableNumber,
     status : req.body.status,
     noOfSeats : req.body.noOfSeats,
     tableType: req.body.tableType
   });
   newTable.save()
       .then( result => {
           console.log('Table created', result);
           res.status(201).json({
               message : 'New table ready',
               tableDetails : {
                   tableNumber : result.tableNumber,
                   tableStatus : result.status,
                   noOfSeats : result.noOfSeats,
                   tableType : result.tableType
               }
           })
       }).catch( err => {
           console.log('Error addding table');
           res.status(500).json({
               message : 'Error adding table',
               error : err
           })
       });
});

router.post('/makeBooking', (req,res,next) => {

});

module.exports = router;