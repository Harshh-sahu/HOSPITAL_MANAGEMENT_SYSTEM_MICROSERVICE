import { Button, PasswordInput, SegmentedControl, TextInput } from "@mantine/core";
import { IconHeartbeat } from "@tabler/icons-react";
import React from "react";
import { useForm } from '@mantine/form';
import { Link } from "react-router-dom";


const RegisterPage = () => {

 const form = useForm({

    initialValues: {
        type:"PATIENT",
      email: '',
      password: '',
      confirmPassword: '',
    },

    validate: {
  email: (value: string) =>
    /^\S+@\S+$/.test(value) ? null : 'Invalid email',

  password: (value: string) =>
    value.length >= 6 ? null : 'Password must be at least 6 characters long',

confirmPassword: (value: string, values: any) =>
    value === values.password ? null : 'Passwords do not match',
},  });

   const handleSubmit = (values: typeof form.values) => {
    console.log(values);
  };

  return (
    <div
      style={{ background: 'url("/hs.jpg")' }}
      className="h-screen !bg-no-repeat !bg-cover !bg-center w-screen flex flex-col items-center justify-center  "
    >
      <div className=" py-3 flex  text-pink-500  gap-1 ">
        <IconHeartbeat size={45} stroke={2.5} />
        <span className="font-handling font-semibold text-4xl">Pulse</span>
      </div>

      <div className="w-[450px] backdrop-blur-md p-10 py-8 rounded-lg ">
        <form onSubmit={form.onSubmit(handleSubmit)} className="flex flex-col gap-5 [&_input]:placeholder-black [&_.mantine-Input-input]:!border-white focus-within:[&_.mantine-Input-input]:!border-pink-400 [&_.mantine-Input-input]:!border [&_input]:pl-2 [&_svg]:text-black [&_input}:!text-white">
          <div className="self-center font-medium bg-fontFamily-heading text-white text-xl">
            Register{" "}
          </div>
           <SegmentedControl bg="none" className="[&_*]:!text-white border border-white" color="pink" {...form.getInputProps("type")} fullWidth size="md" radius="md" data={[{label:'Patient',value:"PATIENT"}, {label:'Doctor',value:"DOCTOR"},{label:'Admin',value:"ADMIN"},]} />
          <TextInput
          className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Email"
              {...form.getInputProps('email')}
          />
          <PasswordInput
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Password"
            className="transition duration-300"
              {...form.getInputProps('password')}
          />
          <PasswordInput
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Confirm Password"
            className="transition duration-300"
              {...form.getInputProps('confirmPassword')}
          />
          <Button radius="md" size="md" type="submit" color="pink">
            register
          </Button>
          <div className="text-sm self-center">
            Already have an account?
            <Link to="/login" className="hover:underline hover:text-blue-700" > Sign In</Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
