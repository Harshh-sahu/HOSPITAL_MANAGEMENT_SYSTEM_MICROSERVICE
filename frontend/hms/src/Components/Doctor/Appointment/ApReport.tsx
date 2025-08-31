import {
  ActionIcon,
  Button,
  Fieldset,
  MultiSelect,
  NumberInput,
  Select,
  Textarea,
  TextInput,
} from "@mantine/core";
import React from "react";
import {
  dosageFrequencies,
  medicineTypes,
  routess,
  symptoms,
  tests,
} from "../../../Data/DropDownData";
import { IconTrash } from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import { createAppointmentReport } from "../../../Service/AppointmentService";
import { errorNotification, successNotification } from "../../../Utility/NotificationUtil";
import { useDispatch } from "react-redux";

const ApReport = ({appointment}:any) => {
  const [loading,setLoading] = React.useState(false);
  const dispatch = useDispatch();
  type Medicine = {
    name: string;
    medicineId?: number;
    dosage: string;
    frequency: string;
    route: string;
    type: string;
    instructions: string;
    prescriptionId?: number;
  };
  const form = useForm<any>({
    initialValues: {
      symptoms: [],
      tests: [],
      diagnosis: '',
      referral: '',
      notes: '',
      prescription: {
   
        medicines: [] as Medicine[],
      },
    },
    validate: {
      symptoms:(value) => (value.length >0) ? null: 'Please Select at least one ',
      diagnosis:(value) => (value.length >0) ? null: 'Diagnosis is required',
      prescription:{

        medicines:{
          name: value=>(value?.trim() ? null: 'Medicine name is required'),
          dosage: value=>(value?.trim() ? null: 'Dosage is required'),
          frequency: value=>(value?.trim() ? null: 'Frequency is required'),
          route: value=>(value?.trim() ? null: 'Route is required'),
          type: value=>(value?.trim() ? null: 'Type is required'),
          instructions: value=>(value?.trim() ? null: 'Instructions are required'),
        }
      }
 
     
   }});
  const insertMedicine = () => {
    form.insertListItem("prescription.medicines", {
      name: "",
      dosage: "",
      frequency: "",
      route: "",
      type: "",
      instructions: "",
    });
  };
  const removeMedicine = (index: number) => {
    form.removeListItem("prescription.medicines", index);
  };
  const handleSubmit = (values: typeof form.values) => {
    console.log("form submitted with value",values);
    let data = {
      ...values,
      doctorId:appointment.doctorId,
      patientId:appointment.patientId,
      appointmentId:appointment.id,
      prescription:{
        ...values.prescription,
            doctorId:appointment.doctorId,
      patientId:appointment.patientId,
      appointmentId:appointment.id,
      }
    }


    setLoading(true);
    createAppointmentReport(data)
    .then((res)=>{
      successNotification("Report created successfully");
      console.log("Report created successfully:", res);
      form.reset();
    }).catch((error) => {
      errorNotification(error?.response?.data?.errorMessage||"Error creating report");
      console.error("Error creating report:", error);
    }).finally(()=>{
      setLoading(false);
    })
  };
  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Fieldset
        className="grid gap-5 grid-cols-2"
        legend={
          <span className="text-lg font-medium text-primary-500">
            Personal information
          </span>
        }
        radius="md"
      >
        <MultiSelect
          {...form.getInputProps("symptoms")}
          className="col-span-2"
          label="Symptoms"
          placeholder="Pick symtoms"
          data={symptoms}
        />
        <MultiSelect
          {...form.getInputProps("tests")}
          className="col-span-2"
          withAsterisk
          label="Tests"
          placeholder="Pick tests"
          data={tests}
        />
        <TextInput
          {...form.getInputProps("diagnosis")}
          label="Diagnosis"
          placeholder="Enter Diagnosis"
          withAsterisk
        />
        <TextInput
          {...form.getInputProps("referral")}
          label="Referral"
          placeholder="Enter Referral details"
        />
        <Textarea
          {...form.getInputProps("notes")}
          className="col-span-2"
          label="Notes"
          placeholder="Enter any additional notes"
      
        />
      </Fieldset>
      <Fieldset
        className="grid gap-5 "
        legend={
          <span className="text-lg font-medium text-primary-500">
            Prescription
          </span>
        }
        radius="md"
      >
   
        {
          form.values.prescription.medicines.map((_medicine:Medicine,index:number)=>        


        (<Fieldset  legend={    <div className=" flex gap-5 items-center">
            <h1 className="text-lg font-medium">Medicine {index + 1}</h1>
            <ActionIcon onClick={()=> removeMedicine(index)} variant="filled" color="red" size="md" className="mb-2">
              <IconTrash />
            </ActionIcon>
          </div>
        } className="grid gap-5 col-span-2 grid-cols-2">
      

          <TextInput
          {...form.getInputProps(`prescription.medicines.${index}.name`)}
            label="Medicine"
            placeholder="Enter medicineName"
            withAsterisk
          />
          <TextInput
          {...form.getInputProps(`prescription.medicines.${index}.dosage`)}
            label="Dosage"
            placeholder="Enter dosage "
            withAsterisk
          />

          <Select
            {...form.getInputProps(`prescription.medicines.${index}.frequency`)}
            label="Frequency"
            placeholder="Pick frequency"
            data={dosageFrequencies}
            withAsterisk
          />
          <NumberInput
            {...form.getInputProps(`prescription.medicines.${index}.duration`)}
            label="Duration (in days)"
            placeholder="Enter duration"
            withAsterisk
          />

          <Select 

            label="Route"
            {...form.getInputProps(`prescription.medicines.${index}.route`)}
            placeholder="Pick route"
            data={routess}
            withAsterisk
          />
          <Select
            label="Type"
            {...form.getInputProps(`prescription.medicines.${index}.type`)}
            placeholder="Select type"
            data={medicineTypes}
            withAsterisk
          />
          <TextInput
            label="Instructions"
            {...form.getInputProps(`prescription.medicines.${index}.instructions`)}
            placeholder="Enter instructions"
            withAsterisk
          />
        </Fieldset>))}
      <div className="flex items-center justify-center col-span-2">
            <Button onClick={insertMedicine} variant="outline" color="blue" className=" col-span-2 mt-4">
          Add Medicine
        </Button>
      </div>
      </Fieldset>
      <div className="flex items-center gap-5 justify-center mt-4">
        <Button loading={loading} type="submit" className="w-full" variant="filled" color="primary">
          Submit Report
        </Button>
        <Button  loading={loading} className="w-full" variant="filled" color="red">
          Cancel
        </Button>
      </div>
    </form>
  );
};

export default ApReport;
