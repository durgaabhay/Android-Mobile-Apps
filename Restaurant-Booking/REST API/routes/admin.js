const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');

const Admin = require('../models/admin');
const checkAuth = require('../auth/check-auth');
const private_secret_key = "JWTAuthValue";

router.get('/', (req,res,next) => {
    res.status(200).json({
        message : 'Inside the admin route to handle get requests'
    });
});

router.post('/signUp', (req,res,next) => {
    console.log('entering the admin route');
    Admin.find({ userRole: req.body.userRole})
    .exec()
        .then(user => {
            if(user.length >= 1){
                return res.status(409).json({
                    message: 'Role exists'
                });
            }else{
                console.log(req.body);
                bcrypt.hash(req.body.password,10,(err,hash) => {
                    if(err) {
                        return res.status(500).json({
                            error: err
                        });
                    }else{
                        const adminUser = new Admin({
                            _id: new mongoose.Types.ObjectId(),
                            userName: req.body.userName,
                            userRole: req.body.userRole,
                            password: hash
                        });
                        adminUser.save().then(result => {
                            console.log(result);
                            res.status(201).json({
                                message: ' User Created ',
                                userDetails: {
                                    LoginName : result.userName,
                                    UserRole : result.userRole
                                }
                            })
                        }).catch(err => {
                            console.log('logging the error' , err);
                            res.status(500).json({
                                error:err
                            })
                        });
                    }
                });
            }
        }).catch( err => {
            console.log('Error creating admin');
            res.status(500).json({
                message : 'Error creating admin user',
                error: err
            });
    });

});

router.post('/login', (req,res,next) => {
    Admin.findOne({userName : req.body.userName}).exec()
        .then( adminUser => {
            console.log('Reading user data' , req.body.password , '  ', adminUser.password);
            if(adminUser.length < 1){//means no user
                res.status(401).json({
                   message : 'Authentication Failed'
                });
            }
            bcrypt.compare(req.body.password,adminUser.password, (err,result) =>{
                if(err){
                    res.status(401).json({
                        message : 'Authentication Failed'
                    });
                }
                if(result){//true if comparison is a success
                    const token = jwt.sign({
                        userId : adminUser._id,
                        userName : adminUser.userName,
                        userRole : adminUser.userRole
                    }, private_secret_key,{expiresIn : "15 days"})
                    res.status(200).json({
                        message : 'Authorization successful',
                        token : token
                    })
                }
                /*//if nothing works still send Auth failed.. bcoz pwds didnt match
                res.status(401).json({
                    message : 'Authentication Failed'
                });*/
            })
        })
        .catch( err => {
           console.log('Error while logging in', err);
           res.status(500).json({
               message : 'Error while logging in',
               error: err
           });
        });
});

router.delete('/:userId', (req,res,next) => {
    Admin.remove({_id : req.body.userId}).exec()
        .then( result => {
            res.status(200).json({
                message: 'User deleted',
                result: result
            });
        })
        .catch( err => {
            res.status(500).json({err})
        });
});

module.exports = router;