import React, { useEffect } from 'react'
import { getAllDoctor } from '../../../Service/DoctorProfileService'
import DoctorCard from './DoctorCard';


const Doctor = () => {

    useEffect(()=>{
getAllDoctor().then((res)=>{
    console.log(res);
    setDoctor(res);
}).catch((error)=>{
    console.error(error);
});
    },[])
    const [Doctor,setDoctor] = React.useState<any[]>([]);


  return (
    <div className='p-5'>
       
       <div className='text-xl  mb-5 text-primary-500 font-semibold'>
          Doctor
       </div>


       <div className='grid grid-cols-4 gap-5 '>
        {Doctor.map((Doctor) => (
        <DoctorCard  key={Doctor.id} {...Doctor}   />
       ))}
       </div>
        
        
    </div>
  )
}

export default Doctor
