import { Avatar, Divider } from '@mantine/core'
import React from 'react'
import { bloodGroupMap } from '../../../Data/DropDownData'
import { IconBriefcase, IconMail, IconMapPin, IconPhone } from '@tabler/icons-react'

const DoctorCard = ({name,email,dob,phone,department,address,specialization,totalExp}:any) => {
  return (
    <div className='border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2'>

<div className='flex items-center gap-3 '>

      <Avatar size='lg' name={name} color='initials' variant='filled' />
      <div>
        <div className='text-sm font-medium'>
        {name}
      </div>

      <div className='text-xs text-gray-500'>
        {specialization} &bull; {department}
      </div>
      </div>
      
</div>
<Divider my="sm" />

<div className='flex  text-xs items-center gap-2'>
    
    <IconMail className='text-gray-500' />
    <div>{email}</div>
</div>
{/* <div className='flex justify-between text-xs items-center gap-2'>
    <div className='text-gray-600'>DOB :</div>
    <div>{dob}</div>
</div> */}
<div className='flex  text-xs items-center gap-2'>
    <IconPhone className='text-gray-600' />
    <div>+91 {phone}</div>
</div>
<div className='flex  text-xs items-center gap-2'>
    <IconMapPin className='text-gray-600'/>
    <div>{address}</div>
</div>

<div className='flex  text-xs items-center gap-2'>
   <IconBriefcase className='text-gray-600' />
    <div>{totalExp} years</div>
</div>
    </div>
  )
}

export default DoctorCard
