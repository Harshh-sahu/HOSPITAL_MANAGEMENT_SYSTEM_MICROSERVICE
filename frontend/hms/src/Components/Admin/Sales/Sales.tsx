import {
  ActionIcon,
  Button,
  Fieldset,
  Group,
  NumberInput,
  Select,
  SelectProps,
  TextInput,
} from "@mantine/core";
import React, { useEffect, useState } from "react";

import {
  IconCheck,
  IconEdit,
  IconPlus,
  icons,
  IconSearchOff,
  IconTrash,
} from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { FilterMatchMode } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { Column } from "primereact/column";
import {

  getAllMedicines,

} from "../../../Service/MedicineService";
import { capitalizeFirstLetter } from "../../../Utility/OtherUtility";

import { addSale } from "../../../Service/SalesService";
import { setegid } from "process";

interface SaleItem {
  medicineId: string;
  quantity: number;
}
const Sales = () => {
  const [loading, setLoading] = React.useState(false);

  const form = useForm<any>({
    initialValues: {
      saleItems: [
        { medicineId: "", quantity: 0 }
      ] as SaleItem[],
    },
    validate: {
      saleItems: {
        medicineId: (value) => (value ? null : "Medicine ID is required"),

        quantity: (value) =>
          value >= 0 ? null : "Quantity cannot be negative",
      },
    },
  });

  const [data, setData] = useState<any[]>([]);
  const [Medicine, setMedicine] = useState<any[]>([]);
  const [edit, setEdit] = useState<boolean>(false);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });

  const cancel = () => {
    form.reset();
    setEdit(false);
  };

  const addMore = () => {
    form.insertListItem("saleItems", { medicineId: "", quantity: 0 });
  };

  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});

  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");

  useEffect(() => {
    getAllMedicines()
      .then((res) => {
        console.log("Fetched stock medicine data:", res);
        setMedicine(res);
        setMedicineMap(
          res.reduce((acc: any, item: any) => {
            acc[item.id] = item;
            return acc;
          }, {})
        );
      })
      .catch((error) => {
        console.error("Error fetching medicine data:", error);
      });
    fetchData();
  }, []);

  const fetchData = () => {
    // getAllStock()
    //   .then((res) => {
    //     console.log("Fetched stock data:", res);
    //     setMedicine(res);
    //   })
    //   .catch((error) => {
    //     console.error("Error fetching medicine data:", error);
    //   });
  };

  const handleSubmit = (values: any) => {
    console.log("form submitted with value", values);

    setLoading(true);

    addSale(values)
      .then((_res) => {
        successNotification("Sale added successfully");
        fetchData();
        form.reset();
        setEdit(false);
      })
      .catch((error) => {
        console.error("Error adding sale:", error);
        errorNotification(`Error adding sale: ${error.message}`);
      })
      .finally(() => setLoading(false));
  };

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;
    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between items-center">
        <Button
          onClick={() => {
            form.reset(); // 👈 ensure add mode clears old id
            setEdit(true);
          }}
          variant="filled"
        >
          Sell Medicine
        </Button>
        <TextInput
          leftSection={<IconSearchOff />}
          fw={400}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
      </div>
    );
  };

  const onEdit = (rowData: any) => {
    setEdit(true);
    console.log("rowData:", rowData);
    form.setValues({
      ...rowData,
      name: rowData.name,
      dosage: rowData.dosage,
      category: rowData.category,
      type: rowData.type,
      manufacturer: rowData.manufacturer,
      unitPrice: rowData.unitPrice,
    });
  };

  const header = renderHeader();

  const renderSelectOption: SelectProps["renderOption"] = ({
    option,
    checked,
  }: any) => (
    <Group flex="1" gap="xs">
      <div className="flex gap-5 items-center">
        {option.label}
        {option?.manufacturer && (
          <span style={{ fontStyle: "italic", color: "gray" }}>
            {option.manufacturer}
          </span>
        )}
      </div>
      {checked && <IconCheck style={{ marginInlineStart: "auto" }} />}
    </Group>
  );

  const actionBodyTemplate = (rowData: any) => {
    return (
      <div className="flex gap-2">
        <ActionIcon onClick={() => onEdit(rowData)}>
          <IconEdit size={20} stroke={1.5} />
        </ActionIcon>
      </div>
    );
  };

  return (
    <div>
      {!edit ? (
        <DataTable
          header={header}
          stripedRows
          size="small"
          value={data}
          paginator
          rows={10}
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          rowsPerPageOptions={[10, 25, 50]}
          dataKey="medicineId"
          filterDisplay="menu"
          globalFilterFields={["name", "manufacturer", "category", "type"]}
          emptyMessage="No Medicine found."
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
        >
          <Column
            field="name"
            header="Medicine"
            body={(rowData) => medicineMap["" + rowData.medicineId]?.name}
          />
          <Column field="dosage" header="Dosage" />
          <Column
            field="category"
            header="Category"
            body={(rowData) => capitalizeFirstLetter(rowData.category) ?? ""}
          />
          <Column
            field="type"
            header="Type"
            body={(rowData) => capitalizeFirstLetter(rowData.type) ?? ""}
          />
          <Column
            field="manufacturer"
            header="Manufacturer"
            body={(rowData) =>
              capitalizeFirstLetter(rowData.manufacturer) ?? ""
            }
          />
          <Column
            field="unitPrice"
            header="Unit Price (₹)"
            sortable
            body={(rowData) => rowData.unitPrice ?? ""}
          />
          <Column
            headerStyle={{ width: "5rem", textAlign: "center" }}
            bodyStyle={{ textAlign: "center", overflow: "visible" }}
            body={actionBodyTemplate}
          />
        </DataTable>
      ) : (
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <Fieldset
            className="grid gap-5"
            legend={
              <span className="text-lg font-medium text-primary-500">
                Medicine information
              </span>
            }
            radius="md"
          >
            <div className="grid gap-4 grid-cols-5">
              {form.values.saleItems.map((item: any, index: number) => (
                <React.Fragment key={index}>
                  <div className="col-span-2">
                    <Select
                      renderOption={renderSelectOption}
                      {...form.getInputProps(`saleItems.${index}.medicineId`)}
                      label="Medicine"
                      placeholder="Select Medicine"
                      data={Medicine.map((item) => ({
                        ...item,
                        value: "" + item.id,
                        label: item.name,
                      }))}
                    />
                  </div>

                  <div className="col-span-2">
                    <NumberInput
                      {...form.getInputProps(`saleItems.${index}.quantity`)}
                      label="Quantity"
                      placeholder="Enter Quantity in stock"
                      min={0}
                      max={50}
                      clampBehavior="strict"
                    />
                  </div>

                  <div  className="flex items-center justify-between">

                     {(item.quantity && item.medicineId) ?<div>
                        Total Price: ₹{item.quantity} X{medicineMap[item.medicineId]?.unitPrice} = ₹
                        {item.quantity *
                          (medicineMap[item.medicineId]?.unitPrice ?? 0)}
                      </div> : null}
                    <ActionIcon color="red" onClick={() => form.removeListItem("saleItems", index)}>

<IconTrash size={16}/>

                    </ActionIcon>
                  </div>
                </React.Fragment>
              ))}
            </div>

            <div className="flex justify-center items-center">
              <Button
              onClick={addMore}
              variant="outline" leftSection={<IconPlus />}>
                
                Add More
              </Button>
            </div>
          </Fieldset>

          <div className="flex items-center gap-5 justify-center mt-4">
            <Button
              loading={loading}
              type="submit"
              className="w-full"
              variant="filled"
              color="primary"
            >
             Sell Medicine
            </Button>
            <Button
              loading={loading}
              className="w-full"
              variant="filled"
              color="red"
              onClick={() => {
                setEdit(false);
              }}
            >
              Cancel
            </Button>
          </div>
        </form>
      )}
    </div>
  );
};

export default Sales;
