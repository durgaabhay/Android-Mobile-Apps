const mongoose = require('mongoose');

const tableSchema = mongoose.Schema({
   _id : mongoose.Schema.Types.ObjectId,
   tableNumber : String,
   userName : String,
   phoneNumber : String,
   noOfSeats : Number,
   status :  {
        type: String,
        enum:['READY','CLEANING','DINING','RESERVED','WAITING','COMPLETE'],
        default:'RESERVED'
   },
    bookingDate: String,
    tableType : {
       type : String,
        enum:['RESERVATION','ALACARTE']
    }
});

module.exports = mongoose.model('Reservation', tableSchema);