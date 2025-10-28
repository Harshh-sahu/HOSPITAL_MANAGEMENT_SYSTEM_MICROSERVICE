import React, { useEffect } from 'react'
import { getAllPatient } from '../../../Service/PatientProfileService'
import PatientCard from './PatientCard';

const Patient = () => {

    useEffect(()=>{
getAllPatient().then((res)=>{
    console.log(res);
    setPatient(res);
}).catch((error)=>{
    console.error(error);
});
    },[])
    const [patient,setPatient] = React.useState<any[]>([]);


  return (
    <div>
       
       <div className='text-xl  mb-5 text-primary-500 font-semibold'>
          Patient
       </div>


       <div className='grid grid-cols-4 gap-5 '>
        {patient.map((patient) => (
        <PatientCard  key={patient.id} {...patient}   />
       ))}
       </div>
        
        
    </div>
  )
}

export default Patient


// <div className='p-5'>
       
//        <div className='text-xl  mb-5 text-primary-500 font-semibold'>
//           Doctor
//        </div>


//        <div className='grid grid-cols-4 gap-5 '>
//         {Doctor.map((Doctor) => (
//         <DoctorCard  key={Doctor.id} {...Doctor}   />
//        ))}
//        </div>
        
        
//     </div>