import { Avatar } from '@mantine/core'
import React from 'react'
import { bloodGroupMap } from '../../../Data/DropDownData'

const PatientCard = ({name,email,dob,phone,id,address,aadharNo,bloodGroup,allergies,chronicDisease}:any) => {
  return (
    <div className='border p-3 flex flex-col gap-2 rounded-lg shadow-sm hover:shadow-md cursor-pointer space-y-2'>

<div className='flex items-center gap-3 '>

      <Avatar name={name} color='initials' variant='filled' />
      <div>
        {name}
      </div>
      
</div>

<div className='flex justify-between text-sm items-center gap-2'>
    <div className='text-gray-600'>Email :</div>
    <div>{email}</div>
</div>
<div className='flex justify-between text-sm items-center gap-2'>
    <div className='text-gray-600'>DOB :</div>
    <div>{dob}</div>
</div>
<div className='flex justify-between text-sm items-center gap-2'>
    <div className='text-gray-600'>Phone :</div>
    <div>{phone}</div>
</div>
<div className='flex justify-between text-sm items-center gap-2'>
    <div className='text-gray-600'>Address :</div>
    <div>{address}</div>
</div>
<div className='flex justify-between text-sm items-center gap-2'>
    <div className='text-gray-600'>Blood Group :</div>
    <div>{bloodGroupMap[bloodGroup]}</div>
</div>
    </div>
  )
}

export default PatientCard

