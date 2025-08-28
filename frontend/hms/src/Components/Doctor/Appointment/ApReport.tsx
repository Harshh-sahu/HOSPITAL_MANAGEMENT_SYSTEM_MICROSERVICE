import { Fieldset, MultiSelect, Textarea, TextInput } from '@mantine/core'
import React from 'react'
import { symptoms, tests } from '../../../Data/DropDownData'

const ApReport = () => {
  return (
    <div>
      <Fieldset className='grid gap-5 grid-cols-2' legend={<span className='text-lg font-medium text-primary-500'>Personal information</span>} radius="md">
       <MultiSelect className='col-span-2' withAsterisk
      label="Symptoms"
      placeholder="Pick symtoms"
      data={symptoms}
    />
       <MultiSelect className='col-span-2' withAsterisk
      label="Tests"
      placeholder="Pick tests"
      data={tests}
    />
    <TextInput label="Diagnosis" placeholder='Enter Diagnosis' withAsterisk />
    <TextInput label="Referral" placeholder='Enter Referral details' withAsterisk />
    <Textarea className='col-span-2' label="Notes" placeholder='Enter any additional notes' withAsterisk />
    </Fieldset>

    </div>
  )
}

export default ApReport
